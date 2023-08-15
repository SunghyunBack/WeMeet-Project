package com.bsh.projectwemeet.services;

import com.bsh.projectwemeet.entities.RecoverContactCodeEntity;
import com.bsh.projectwemeet.entities.UserEntity;
import com.bsh.projectwemeet.enums.SendRecoverContactCodeResult;
import com.bsh.projectwemeet.enums.SendRecoverEmailNameResult;
import com.bsh.projectwemeet.enums.VeryfiRecoverContactCodeResult;
import com.bsh.projectwemeet.mappers.RecoverAccountMapper;
import com.bsh.projectwemeet.utils.CryptoUtil;
import com.bsh.projectwemeet.utils.NCloudUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RecoverAccountService {
    private final RecoverAccountMapper recoverAccountMapper;

    @Autowired
    public RecoverAccountService(RecoverAccountMapper recoverAccountMapper) {
        this.recoverAccountMapper = recoverAccountMapper;
    }

    // 연락처 입력후 인증번호 보내기 클릭할때를 위한 코드
    public SendRecoverContactCodeResult sendRecoverContactCode(RecoverContactCodeEntity recoverContactCode) {
//        웹페이지에서 입력한 연락처와 이름의 값들이 조건식에 부합한지 안하는지에 대한 코드이다.
        if (recoverContactCode == null ||
                recoverContactCode.getContact() == null ||
                !recoverContactCode.getContact().matches("^(010)(\\d{8})$") ||
                recoverContactCode.getName() == null) {
            return SendRecoverContactCodeResult.FAILURE;
        }

// 데이터 베이스의 값들을 existingUser 변수에 반환한후 값들이 있는지 없는지 확인하는 코드이다.
        UserEntity existingUser = this.recoverAccountMapper.selectUserByContactName(recoverContactCode.getContact(), recoverContactCode.getName());
        if (existingUser == null) {
            return SendRecoverContactCodeResult.FAILURE;
        }

//        인증번호를 보내야 하기에 멤버변수에 code의 난수를 만들고, salt에 hash512를 적용하며 생성시간, 인증완료 시간, 인증(참,거짓)을 설정한다.
        recoverContactCode.setCode(RandomStringUtils.randomNumeric(6))
                .setSalt(CryptoUtil.hashSha512(String.format("%s%s%f%f",
                        recoverContactCode.getCode(),
                        recoverContactCode.getContact(),
                        Math.random(),
                        Math.random())))
                .setCreatedAt(new Date())
                .setExpiresAt(DateUtils.addMinutes(recoverContactCode.getCreatedAt(), 5))
                .setExpired(false)
                .setName(recoverContactCode.getName());

//        네이버 n클라우드 API를 이용해서 문자의 내용을 전한다.
        NCloudUtil.sendSms(recoverContactCode.getContact(), String.format("[WeMeet 계정찾기] 이메일 찾기 인증번호 [%s]를 입력해 주세요.", recoverContactCode.getCode()));

//        위의 failure조건식에 만족하지 않고 데이터베이스에 값들이 잘 들어갔다면 success를 아니면 failure를 리턴한다.
        return this.recoverAccountMapper.insertRecoverContactCode(recoverContactCode) > 0
                ? SendRecoverContactCodeResult.SUCCESS
                : SendRecoverContactCodeResult.FAILURE;

    }

    //    인증번호를 보낸 이후 사용할 코드(연락처,인증번호,salt를  데이터베이스와 비교해야한다)
    public VeryfiRecoverContactCodeResult verifyRecoverContactCodeResult(RecoverContactCodeEntity recoverContactCode) {
//        웹페이지에서 보낸 값들(연락처,코드,salt)의 값들이 정규식에 맞는지 아닌지 비교한다.
        if (recoverContactCode == null ||
                recoverContactCode.getContact() == null ||
                recoverContactCode.getCode() == null ||
                recoverContactCode.getSalt() == null ||
                !recoverContactCode.getContact().matches("^(010\\d{8})$") ||
                !recoverContactCode.getCode().matches("^(\\d{6})$") ||
                !recoverContactCode.getSalt().matches("^([\\da-f]{128})$")) {
            return VeryfiRecoverContactCodeResult.FAILURE;
        }
//    웹페이지에서 보낸 값들을 데이터베이스와 비교해 조회한것을 변수 recoverContactCode에 반환한다.
        recoverContactCode = this.recoverAccountMapper.selectRecoverContactCodeByCodeByContactCodeSalt(recoverContactCode);
// 반환된 recoverContactCode의 값이 있는지? 만료시간을 지켰는지 확인한후 이상이 없으면 setExpired값에 true를 준다.
        if (recoverContactCode == null) {
            return VeryfiRecoverContactCodeResult.FAILURE;
        }
        if (new Date().compareTo(recoverContactCode.getExpiresAt()) > 0) {
            return VeryfiRecoverContactCodeResult.FAILURE_EXPIRED;
        }
        recoverContactCode.setExpired(true);

//        최종적으로 데이터베이스의 값들이 수정이 되었다면 success를 아니면 failure를 준다.
        return this.recoverAccountMapper.updateRecoverContactCode(recoverContactCode) > 0
                ? VeryfiRecoverContactCodeResult.SUCCESS
                : VeryfiRecoverContactCodeResult.FAILURE;
    }

    //    데이터 베이스의 값중 연락처와 이름을 조회한후 있는지 없는지에 대한 코드이다.
    public UserEntity getUserByContactName(String contact, String name) {
        return this.recoverAccountMapper.selectUserByContactName(contact, name);
    }


}
