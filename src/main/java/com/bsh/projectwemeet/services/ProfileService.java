package com.bsh.projectwemeet.services;

import com.bsh.projectwemeet.entities.ArticleEntity;
import com.bsh.projectwemeet.entities.ProfileEntity;
import com.bsh.projectwemeet.entities.RegisterContactCodeEntity;
import com.bsh.projectwemeet.entities.UserEntity;
import com.bsh.projectwemeet.enums.*;
import com.bsh.projectwemeet.mappers.ProfileMapper;
import com.bsh.projectwemeet.utils.CryptoUtil;
import com.bsh.projectwemeet.utils.NCloudUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

@Service
public class ProfileService {
    private final ProfileMapper profileMapper;

    @Autowired
    public ProfileService(ProfileMapper profileMapper) {
        this.profileMapper = profileMapper;
    }

    public UserEntity getAll(HttpSession session) {

        UserEntity user = (UserEntity) session.getAttribute("user");

        return this.profileMapper.selectAll(user.getEmail());
    }

    public UserEntity getUserByNickName(String nickname) {
        return profileMapper.selectNickName(nickname);
    }


    public ArticleEntity[] getCountCategoryByPage(String nickname) {

        UserEntity user = profileMapper.selectNickName(nickname);

        return this.profileMapper.selectCountCategoryByPage(user.getEmail());
    }

    public ArticleEntity getArticleImage(int index) {
        ArticleEntity article = this.profileMapper.selectThumbnailLink(index);

        return article;
    }

    public ProfileEntity getThumbnail(String nickname) {
        UserEntity user = profileMapper.selectNickName(nickname);
        ProfileEntity profile = profileMapper.selectThumbnail(user.getEmail());

        return profile == null
                ? null
                : profile;
    }


    public DeleteUserResult deleteThumbnailResult(HttpSession session, ProfileEntity profile) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return DeleteUserResult.FAILURE;
        }

        ClassPathResource resource = new ClassPathResource("profile.png");
        byte[] defaultProfileImageBytes = null;

        try (InputStream inputStream = resource.getInputStream()) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            defaultProfileImageBytes = outputStream.toByteArray();

            profile.setProfileThumbnail(defaultProfileImageBytes)
                    .setProfileThumbnailMime("image/png");// 이미지의 MIME 타입을 설정해야 합니다.

        } catch (IOException e) {

        } //회원가입시 같이 프로필 추가되게
        return this.profileMapper.deleteThumbnail(profile) > 0
                ? DeleteUserResult.SUCCESS
                : DeleteUserResult.FAILURE;
    }


    public LoginResult checkPassword(UserEntity user) {
        if (user.getPassword() == null) {
            return LoginResult.FAILURE;
        }

        // 데이터베이스에서 해당 이메일에 해당하는 사용자 정보를 가져옴
        UserEntity existingUser = this.profileMapper.selectPasswordByEmail(user.getEmail());

        // 입력받은 비밀번호를 해시화하여 사용자의 비밀번호와 비교
        String hashedPassword = CryptoUtil.hashSha512(user.getPassword());
        if (existingUser != null && existingUser.getPassword().equals(hashedPassword)) {
            return LoginResult.SUCCESS; // 비밀번호 일치로 인증 성공
        } else {
            return LoginResult.FAILURE; // 비밀번호 불일치로 인증 실패
        }
    }


    @Transactional
    public boolean putProfile(ProfileEntity profile) {
        return this.profileMapper.updateThumbnail(profile) > 0;
    }


    public int getArticleIndexCountByEmail(HttpSession session) {
        UserEntity loginUser = (UserEntity) session.getAttribute("user");
        Integer articleIndexCount = profileMapper.getArticleIndexCountByEmail(loginUser.getEmail());

        // articleIndexCount가 null인 경우 0으로 처리합니다.
        return articleIndexCount != null ? articleIndexCount : 0;
    }


    //연락처 코드 전송
    public SendRegisterContactCodeResult sendContactCodeResult(RegisterContactCodeEntity registerContactCode) {

        if (registerContactCode == null ||
                registerContactCode.getContact() == null ||
                !registerContactCode.getContact().matches("^(010)(\\d{8})$")) {
            return SendRegisterContactCodeResult.FAILURE;
        }


        if (registerContactCode.isExpired()) {
            return SendRegisterContactCodeResult.FAILURE;
        }

        if (this.profileMapper.selectUserByContact(registerContactCode.getContact()) != null) {
            return SendRegisterContactCodeResult.FAILURE;
        }
        String code = RandomStringUtils.randomNumeric(6);
        String salt = CryptoUtil.hashSha512(String.format("%s%s%f%f",
                registerContactCode.getCode(),
                code,
                Math.random(),
                Math.random()));
        Date createdAt = new Date();
        Date expiresAt = DateUtils.addMinutes(createdAt, 5);
        registerContactCode.setCode(code).setSalt(salt).setCreatedAt(createdAt).setExpiresAt(expiresAt).setExpired(false);

        NCloudUtil.sendSms(registerContactCode.getContact(), String.format("[WeMeet 연락처 변경] 연락처 변경 인증번호 [%s]를 입력해 주세요.", registerContactCode.getCode()));

        return this.profileMapper.insertContactCode(registerContactCode) > 0
                ? SendRegisterContactCodeResult.SUCCESS
                : SendRegisterContactCodeResult.FAILURE;
    }

    //인증코드 확인
    public VerifyRegisterContactCodeResult verifyRegisterContactCodeResult(RegisterContactCodeEntity registerContactCode) {
        registerContactCode = this.profileMapper.selectContactCodeByContactCodeSalt(registerContactCode);
        if (registerContactCode == null) {
            return VerifyRegisterContactCodeResult.FAILURE;
        }
        if (new Date().compareTo(registerContactCode.getExpiresAt()) > 0) {
            return VerifyRegisterContactCodeResult.FAILURE_EXPIRED;
        }
        registerContactCode.setExpired(true);
        return this.profileMapper.updateContactCode(registerContactCode) > 0
                ? VerifyRegisterContactCodeResult.SUCCESS
                : VerifyRegisterContactCodeResult.FAILURE;
    }

    public ModifyPasswordResult resetNickname(String nickname, UserEntity user, HttpSession session) {
        if (!(session.getAttribute("user") instanceof UserEntity)) {
            return ModifyPasswordResult.FAILURE;
        }
        UserEntity signedUser = (UserEntity) session.getAttribute("user");

        if (nickname.equals(signedUser.getNickname())) {
            return ModifyPasswordResult.FAILURE_PASSWORD_MISMATCH;
        }
        user.setNickname(nickname);
        return this.profileMapper.updateNickname(user) > 0
                ? ModifyPasswordResult.SUCCESS : ModifyPasswordResult.FAILURE;
    }

    //비밀번호 변경
    public ModifyPasswordResult modifyPassword(String password, UserEntity user, HttpSession session) {

        if (!(session.getAttribute("user") instanceof UserEntity)) {
            return ModifyPasswordResult.FAILURE; //세션에서 user를 가져옴, 로그인 안 함
        }
        UserEntity signedUser = (UserEntity) session.getAttribute("user");
        password = CryptoUtil.hashSha512(password);
        if (password.equals(signedUser.getPassword())) {
            return ModifyPasswordResult.FAILURE_PASSWORD_MISMATCH; // 입력한 기존 비밀번호가 현재 비밀번호랑 다름
        }
        user.setPassword(password);
        return this.profileMapper.updatePassword(user) > 0
                ? ModifyPasswordResult.SUCCESS : ModifyPasswordResult.FAILURE;
    }

    // 연락처 변경
    public ModifyPasswordResult resetContact(String contact, UserEntity user, HttpSession session) {
        if (!(session.getAttribute("user") instanceof UserEntity)) {
            return ModifyPasswordResult.FAILURE;
        }
        UserEntity signedUser = (UserEntity) session.getAttribute("user");

        if (contact.equals(signedUser.getContact())) {
            return ModifyPasswordResult.FAILURE_PASSWORD_MISMATCH;
        }
        user.setContact(contact);
        return this.profileMapper.updateContact(user) > 0
                ? ModifyPasswordResult.SUCCESS : ModifyPasswordResult.FAILURE;
    }

    //소개글 변경
    public boolean resetContent(ProfileEntity profile, String content) {
        profile.setIntroduceText(content);
        return this.profileMapper.updateContent(profile) > 0;
    }

    // 주소 변경
    public ModifyPasswordResult resetAddress(String postal, String primary, String secondary, UserEntity user, HttpSession session) {
        if (!(session.getAttribute("user") instanceof UserEntity)) {
            return ModifyPasswordResult.FAILURE;
        }
        user.setAddressPostal(postal)
                .setAddressPrimary(primary)
                .setAddressSecondary(secondary);
        return this.profileMapper.updateAddress(user) > 0
                ? ModifyPasswordResult.SUCCESS : ModifyPasswordResult.FAILURE;
    }

    //프로필 이미지 삭제

    // 회원탈퇴
    public DeleteUserResult deleteUserResult(HttpSession session) {
        UserEntity loggedInUser = (UserEntity) session.getAttribute("user");
        if (loggedInUser == null) {
            return DeleteUserResult.FAILURE;
        }
        return this.profileMapper.deleteUser(loggedInUser.getEmail()) > 0
                ? DeleteUserResult.SUCCESS
                : DeleteUserResult.FAILURE;
    }
}
