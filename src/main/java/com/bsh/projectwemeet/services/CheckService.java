package com.bsh.projectwemeet.services;

import com.bsh.projectwemeet.entities.RecoverEmailCodeEntity;
import com.bsh.projectwemeet.entities.UserEntity;
import com.bsh.projectwemeet.enums.RecoverPasswordResult;
import com.bsh.projectwemeet.enums.SendRecoverEmailCodeResult;
import com.bsh.projectwemeet.enums.VerifyRecoverEmailCodeResult;
import com.bsh.projectwemeet.mappers.CheckMapper;
import com.bsh.projectwemeet.utils.CryptoUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;

@Service
public class CheckService {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine springTemplateEngine;
    private final CheckMapper checkMapper;


    @Autowired
    public CheckService(JavaMailSender javaMailSender, SpringTemplateEngine springTemplateEngine, CheckMapper checkMapper) {
        this.javaMailSender = javaMailSender;
        this.springTemplateEngine = springTemplateEngine;
        this.checkMapper = checkMapper;
    } //이메일과 템플릿엔진 check(이메일과 비밀번호 설정 관련)Mapper

    public SendRecoverEmailCodeResult sendRecoverEmailCode(RecoverEmailCodeEntity recoverEmailCode) throws MessagingException {
        if (recoverEmailCode == null ||
                recoverEmailCode.getEmail() == null ||
                !recoverEmailCode.getEmail().matches("^(?=.{10,50}$)([\\da-zA-Z\\-_\\.]{5,25})@([\\da-z][\\da-z\\-]*[\\da-z]\\.)?([\\da-z][\\da-z\\-]*[\\da-z])\\.([a-z]{2,15})(\\.[a-z]{2})?$")) {
            return SendRecoverEmailCodeResult.FAILURE;
        } //이메일 규칙에 적합하지 않거나 null(정확히 빈값)을 제출시 실패를 반환한다.

        if (this.checkMapper.selectUserByEmail(recoverEmailCode.getEmail()) == null) {
            return SendRecoverEmailCodeResult.FAILURE;
        } //찾으려는 이메일이 회원가입되있는 이메일인가 찾고 없다면 실패 반환

        recoverEmailCode
                .setCode(RandomStringUtils.randomAlphanumeric(6)) //6자리의 랜덤인증번호
                .setSalt(CryptoUtil.hashSha512(String.format("%s%s%f%f",
                        recoverEmailCode.getCode(),
                        recoverEmailCode.getEmail(),
                        Math.random(),
                        Math.random())))
                // 생성되는 솔트 값은 인증 과정에서 사용되는 값으로, 이메일과 인증번호, 그리고 임의의 난수 값을 조합하여 생성됩니다.
                .setCreatedAt(new Date()) // 현재시간
                .setExpiresAt(DateUtils.addHours(recoverEmailCode.getCreatedAt(), 1))
                .setExpired(false); //아직 인증을 거치지않았기에 false값
        String url = String.format("http://wemeet.sunghyunback.com/recoverAccount/recoverPassword?email=%s&salt=%s",
                recoverEmailCode.getEmail(),
                recoverEmailCode.getSalt());
        // 현재시간을 설정해서 현재시간에서 1시간 제한을 둔다.
        // 재설정 url에 사용자의 개인정보(이메일,salt)를 담는 이유는 이메일과 임시로 생성된 솔트 값을 함께 사용하여 인증 절차를 거치게 되는 과정입니다.
        // url 변수에 이메일 값과 솔트 값이 담기는 이유는 비밀번호 재설정을 위한 URL을 생성하기 위해서입니다.

        Context context = new Context();
        context.setVariable("url", url); //Context 변수에 인증을 위한 url을 담고

        String code = recoverEmailCode.getCode();
        context.setVariable("code", code); //context 변수에 인증을 위한 코드를 담는다.

        MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setSubject("[WE - MEET 비밀번호 재설정] 이메일 인증"); //메일의 제목
        mimeMessageHelper.setFrom("tjdgus4869@naver.com"); //이메일을 보내는 발신자
        mimeMessageHelper.setTo(recoverEmailCode.getEmail()); //이메일 수신자로 설정할 이메일 주소
        mimeMessageHelper.setText(this.springTemplateEngine.process("/user/SendEmailCode", context), true);
        //위에꺼는 제목 및 발신,수신자고 본문에 들어갈 내용을  /user/SendEmailCode(본문) true는 html이라는 의미이다.
        //css가 안되는 이유는 외부나 내부 스타일시트를 이메일 클라이언트에서 지원하지 않기 때문입니다.
        //이유는 이메일 클라이언트의 보안 및 호환성 제약 사항 때문이다.
        this.javaMailSender.send(mimeMessage);

        return this.checkMapper.insertRecoverEmailCode(recoverEmailCode) > 0
                ? SendRecoverEmailCodeResult.SUCCESS
                : SendRecoverEmailCodeResult.FAILURE; //성공시 recover_email_codes에 레코드 추가
    }


    public VerifyRecoverEmailCodeResult verifyRecoverEmailCode(RecoverEmailCodeEntity recoverEmailCode) {
        if (recoverEmailCode == null ||
                recoverEmailCode.getEmail() == null ||
                recoverEmailCode.getCode() == null ||
                recoverEmailCode.getSalt() == null ||
                !recoverEmailCode.getEmail().matches("^(?=.{10,50}$)([\\da-zA-Z\\-_.]{5,25})@([\\da-z][\\da-z\\-]*[\\da-z]\\.)?([\\da-z][\\da-z\\-]*[\\da-z])\\.([a-z]{2,15})(\\.[a-z]{2})?$") ||
                !recoverEmailCode.getCode().matches("^([\\da-zA-Z]{6})$") ||
                !recoverEmailCode.getSalt().matches("^([\\da-f]{128})$")) {
            return VerifyRecoverEmailCodeResult.FAILURE;
        }  // 입력값으로 검증된 recoverEmailCode를 데이터베이스에서 조회

        recoverEmailCode = this.checkMapper.selectRecoverEmailCodeByEmailCodeSalt(recoverEmailCode);
        if (recoverEmailCode == null) {
            return VerifyRecoverEmailCodeResult.FAILURE;
        } // 조회된 recoverEmailCode가 없을 경우 실패 반환

        if (new Date().compareTo(recoverEmailCode.getExpiresAt()) > 0) {
            return VerifyRecoverEmailCodeResult.FAILURE_EXPIRED;
        } //현재시간과 만료시간을 비교하여 만료된 경우 실패 반환

        return this.checkMapper.updateRecoverEmailCode(recoverEmailCode) > 0
                ? VerifyRecoverEmailCodeResult.SUCCESS
                : VerifyRecoverEmailCodeResult.FAILURE;
    } // recoverEmailCode를 업데이트하고 업데이트된 행의 개수를 확인하여 성공 또는 실패 반환


    public VerifyRecoverEmailCodeResult recoverEmailCodeResult(RecoverEmailCodeEntity recoverEmailCode) {
        if (recoverEmailCode == null ||
                recoverEmailCode.getEmail() == null ||
                recoverEmailCode.getCode() == null ||
                recoverEmailCode.getSalt() == null ||
                !recoverEmailCode.getEmail().matches("^(?=.{10,50}$)([\\da-zA-Z\\-_.]{5,25})@([\\da-z][\\da-z\\-]*[\\da-z]\\.)?([\\da-z][\\da-z\\-]*[\\da-z])\\.([a-z]{2,15})(\\.[a-z]{2})?$") ||
                !recoverEmailCode.getCode().matches("^([\\da-zA-Z]{6})$") ||
                !recoverEmailCode.getSalt().matches("^([\\da-f]{128})$")) {
            return VerifyRecoverEmailCodeResult.FAILURE;
        }
        recoverEmailCode = this.checkMapper.selectRecoverEmailCodeByEmailCodeSalt(recoverEmailCode);
        if (recoverEmailCode == null) {
            return VerifyRecoverEmailCodeResult.FAILURE;
        }
        if (new Date().compareTo(recoverEmailCode.getExpiresAt()) > 0) {
            return VerifyRecoverEmailCodeResult.FAILURE_EXPIRED;
        }
        recoverEmailCode.setExpired(true); //번호 맞을시 재인증 불가능하게 true로 변경
        return this.checkMapper.updateRecoverEmailCode(recoverEmailCode) > 0
                ? VerifyRecoverEmailCodeResult.SUCCESS
                : VerifyRecoverEmailCodeResult.FAILURE_EXPIRED;
    } //위의 코드랑 비슷


    public RecoverPasswordResult recoverPassword(RecoverEmailCodeEntity recoverEmailCode, UserEntity user) {
        if (recoverEmailCode == null ||
                recoverEmailCode.getEmail() == null ||
                recoverEmailCode.getCode() == null ||
                recoverEmailCode.getSalt() == null ||
                user == null ||
                user.getPassword() == null) {
            return RecoverPasswordResult.FAILURE;
        }  // 입력값의 유효성 검사

        recoverEmailCode = this.checkMapper.selectRecoverEmailCodeByEmailCodeSalt(recoverEmailCode);
        // 입력값으로 검증된 recoverEmailCode를 데이터베이스에서 조회

        if (recoverEmailCode == null || !recoverEmailCode.isExpired()) {
            return RecoverPasswordResult.FAILURE;
        } // 조회된 recoverEmailCode가 없거나 만료되지 않은 경우 실패 반환

        String newPassword = CryptoUtil.hashSha512(user.getPassword());
        // 새로운 비밀번호를 해싱하여 설정

        user = this.checkMapper.selectUserByEmail(user.getEmail());
        // 이메일로 사용자 정보를 조회

        if (user == null) {
            return RecoverPasswordResult.FAILURE;
        } // 조회된 사용자가 없는 경우 실패 반환

        user.setPassword(newPassword);
        return this.checkMapper.updateUser(user) > 0 && this.checkMapper.deleteRecoverEmailCode(recoverEmailCode) > 0
                ? RecoverPasswordResult.SUCCESS
                : RecoverPasswordResult.FAILURE;
        // 비밀번호 업데이트 및 recoverEmailCode 삭제 후 결과 반환
    }
}
