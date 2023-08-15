package com.bsh.projectwemeet.services;

import com.bsh.projectwemeet.entities.ProfileEntity;
import com.bsh.projectwemeet.enums.*;
import com.bsh.projectwemeet.mappers.ProfileMapper;
import com.bsh.projectwemeet.mappers.RegisterMapper;
import com.bsh.projectwemeet.entities.RegisterContactCodeEntity;
import com.bsh.projectwemeet.entities.UserEntity;
import com.bsh.projectwemeet.utils.CryptoUtil;
import com.bsh.projectwemeet.utils.NCloudUtil;


import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Service
public class RegisterService {

    private final RegisterMapper registerMapper;
    private final ProfileMapper profileMapper;

    @Autowired
    public RegisterService(RegisterMapper registerMapper, ProfileMapper profileMapper) {
        this.registerMapper = registerMapper;
        this.profileMapper = profileMapper;
    }



    public SendRegisterContactCodeResult sendRegisterContactCodeResult(RegisterContactCodeEntity registerContactCode){
        if (registerContactCode == null
        || registerContactCode.getContact() == null
        || !registerContactCode.getContact().matches("^(010)(\\d{8})$")){ //잘못된 전화번호 or 값이 입력되어 있지 않을때
            return SendRegisterContactCodeResult.FAILURE;
        }

        if (this.registerMapper.selectUserByContact(registerContactCode.getContact()) !=null){ //사용중인 연락처일 때
            return SendRegisterContactCodeResult.FAILURE_DUPLICATE;
        }
        
        String code = RandomStringUtils.randomNumeric(6); //랜덤 숫자 6자리 (인증번호)
        String salt = CryptoUtil.hashSha512(String.format("%s%s%f%f", //비밀번호 암호화
                registerContactCode.getCode(),
                code,
                Math.random(),
                Math.random()));
        Date createdAt = new Date(); //시간제한을 걸기위한 Date값
        Date expiresAt = DateUtils.addMinutes(createdAt,5); //시간제한 5분
        registerContactCode.setCode(code).setSalt(salt).setCreatedAt(createdAt).setExpiresAt(expiresAt).setExpired(false);

        NCloudUtil.sendSms(registerContactCode.getContact(), String.format("[We Meet 회원가입] 인증번호[%s]를 입력해주세요",registerContactCode.getCode()));

        return this.registerMapper.insertRegisterContactCode(registerContactCode)>0
                ? SendRegisterContactCodeResult.SUCCESS
                : SendRegisterContactCodeResult.FAILURE;
    }



    public VerifyRegisterContactCodeResult verifyRegisterContactCodeResult(RegisterContactCodeEntity registerContactCode){
        registerContactCode = this.registerMapper.selectRegisterContactCodeByContactSalt(registerContactCode);
        if (registerContactCode == null){
            return VerifyRegisterContactCodeResult.FAILURE;
        }
        if (new Date().compareTo(registerContactCode.getExpiresAt())>0){
            return VerifyRegisterContactCodeResult.FAILURE_EXPIRED; //인증번호 만료
        }
        registerContactCode.setExpired(true);
        return this.registerMapper.updateRegisterCode(registerContactCode)>0
                ? VerifyRegisterContactCodeResult.SUCCESS
                : VerifyRegisterContactCodeResult.FAILURE;
    }


    public RegisterResult register(UserEntity user,RegisterContactCodeEntity registerContactCode,ProfileEntity profile)throws NoSuchAlgorithmException {

        if (this.registerMapper.selectUserByEmail(user.getEmail()) != null) {
            return RegisterResult.FAILURE_DUPLICATE_EMAIL; //사용중인 이메일
        }
        if (this.registerMapper.selectUserByContact(user.getContact()) != null) {
            return RegisterResult.FAILURE_DUPLICATE_CONTACT; //사용중인 연락처
        }
        if (this.registerMapper.selectUserByNickname(user.getNickname()) != null) {
            return RegisterResult.FAILURE_DUPLICATE_NICKNAME; //사용 중인 닉네임
        }


        // 닉네임에 욕설 필터링 추가
        String[] abusiveWords = {"ㅅㅂ", "ㅆㅂ", "시발", "씨발", "10새", "10새기", "10새리", "10세리", "10쉐이", "10쉑", "10스", "10쌔",
                "10쌔기", "10쎄", "10알", "10창", "10탱", "18것", "18넘", "18년", "18노", "18놈",
                "18뇬", "18럼", "18롬", "18새", "18새끼", "18색", "18세끼", "18세리", "18섹", "18쉑", "18스", "18아",
                "c파", "c팔", "fuck", "ㄱㅐ", "ㄲㅏ", "ㄲㅑ", "ㄲㅣ", "ㅅㅂㄹㅁ", "ㅅㅐ", "ㅆㅂㄹㅁ", "ㅆㅍ", "ㅆㅣ", "ㅆ앙", "ㅍㅏ", "凸",
                "갈보", "갈보년", "강아지", "같은년", "같은뇬", "개같은", "개구라", "개년", "개놈",
                "개뇬", "개대중", "개독", "개돼중", "개랄", "개보지", "개뻥", "개뿔", "개새", "개새기", "개새끼",
                "개새키", "개색기", "개색끼", "개색키", "개색히", "개섀끼", "개세", "개세끼", "개세이", "개소리", "개쑈",
                "개쇳기", "개수작", "개쉐", "개쉐리", "개쉐이", "개쉑", "개쉽", "개스끼", "개시키", "개십새기",
                "개십새끼", "개쐑", "개씹", "개아들", "개자슥", "개자지", "개접", "개좆", "개좌식", "개허접", "걔새",
                "걔수작", "걔시끼", "걔시키", "걔썌", "걸레", "게색기", "게색끼", "광뇬", "구녕", "구라", "구멍",
                "그년", "그새끼", "냄비", "놈현", "뇬", "눈깔", "뉘미럴", "니귀미", "니기미", "니미", "니미랄", "니미럴",
                "니미씹", "니아배", "니아베", "니아비", "니어매", "니어메", "니어미", "닝기리", "닝기미", "대가리",
                "뎡신", "도라이", "돈놈", "돌아이", "돌은놈", "되질래", "뒈져", "뒈져라", "뒈진", "뒈진다", "뒈질",
                "뒤질래", "등신", "디져라", "디진다", "디질래", "딩시", "따식", "때놈", "또라이", "똘아이", "똘아이",
                "뙈놈", "뙤놈", "뙨넘", "뙨놈", "뚜쟁", "띠바", "띠발", "띠불", "띠팔", "메친넘", "메친놈", "미췬",
                "미췬", "미친", "미친넘", "미친년", "미친놈", "미친새끼", "미친스까이", "미틴", "미틴넘", "미틴년",
                "미틴놈", "바랄년", "병자", "뱅마", "뱅신", "벼엉신", "병쉰", "병신", "부랄", "부럴", "불알", "불할", "붕가",
                "붙어먹", "뷰웅", "븅", "븅신", "빌어먹", "빙시", "빙신", "빠가", "빠구리", "빠굴", "빠큐", "뻐큐",
                "뻑큐", "뽁큐", "상넘이", "상놈을", "상놈의", "상놈이", "새갸", "새꺄", "새끼", "새새끼", "새키",
                "색끼", "생쑈", "세갸", "세꺄", "세끼", "섹스", "쇼하네", "쉐", "쉐기", "쉐끼", "쉐리", "쉐에기",
                "쉐키", "쉑", "쉣", "쉨", "쉬발", "쉬밸", "쉬벌", "쉬뻘", "쉬펄", "쉽알", "스패킹", "스팽", "시궁창", "시끼",
                "시댕", "시뎅", "시랄", "시발", "시벌", "시부랄", "시부럴", "시부리", "시불", "시브랄", "시팍",
                "시팔", "시펄", "신발끈", "심발끈", "심탱", "십8", "십라", "십새", "십새끼", "십세", "십쉐", "십쉐이", "십스키",
                "십쌔", "십창", "십탱", "싶알", "싸가지", "싹아지", "쌉년", "쌍넘", "쌍년", "쌍놈", "쌍뇬", "쌔끼",
                "쌕", "쌩쑈", "쌴년", "썅", "썅년", "썅놈", "썡쇼", "써벌", "썩을년", "썩을놈", "쎄꺄", "쎄엑",
                "쒸벌", "쒸뻘", "쒸팔", "쒸펄", "쓰바", "쓰박", "쓰발", "쓰벌", "쓰팔", "씁새", "씁얼", "씌파", "씨8",
                "씨끼", "씨댕", "씨뎅", "씨바", "씨바랄", "씨박", "씨발", "씨방", "씨방새", "씨방세", "씨밸", "씨뱅",
                "씨벌", "씨벨", "씨봉", "씨봉알", "씨부랄", "씨부럴", "씨부렁", "씨부리", "씨불", "씨붕", "씨브랄",
                "씨빠", "씨빨", "씨뽀랄", "씨앙", "씨파", "씨팍", "씨팔", "씨펄", "씸년", "씸뇬", "씸새끼", "씹같", "씹년",
                "씹뇬", "씹보지", "씹새", "씹새기", "씹새끼", "씹새리", "씹세", "씹쉐", "씹스키", "씹쌔", "씹이", "씹자지",
                "씹질", "씹창", "씹탱", "씹퇭", "씹팔", "씹할", "씹헐", "아가리", "아갈", "아갈이", "아갈통",
                "아구창", "아구통", "아굴", "얌마", "양넘", "양년", "양놈", "엄창", "엠병", "여물통", "염병", "엿같", "옘병",
                "옘빙", "오입", "왜년", "왜놈", "욤병", "육갑", "은년", "을년", "이년", "이새끼", "이새키", "이스끼",
                "이스키", "임마", "자슥", "잡것", "잡넘", "잡년", "잡놈", "저년", "저새끼", "접년", "젖밥", "조까",
                "조까치", "조낸", "조또", "조랭", "조빠", "조쟁이", "조지냐", "조진다", "조찐", "조질래", "존나", "존나게", "존니", "존만",
                "존만한", "좀물", "좁년", "좁밥", "좃까", "좃또", "좃만", "좃밥", "좃이", "좃찐", "좆같", "좆까", "좆나",
                "좆또", "좆만", "좆밥", "좆이", "좆찐", "좇같", "좇이", "좌식", "주글", "주글래", "주데이", "주뎅",
                "주뎅이", "주둥아리", "주둥이", "주접", "주접떨", "죽고잡", "죽을래", "죽통", "쥐랄", "쥐롤",
                "쥬디", "지랄", "지럴", "지롤", "지미랄", "짜식", "짜아식", "쪼다", "쫍빱", "찌랄", "창녀", "캐년",
                "캐놈", "캐스끼", "캐스키", "캐시키", "탱구", "팔럼", "퍽큐", "호로", "호로놈", "호로새끼",
                "호로색", "호로쉑", "호로스까이", "호로스키", "후라들", "후래자식", "후레"}; // 욕설 단어들의 배열

        for (String abusiveWord : abusiveWords) {
            if (user.getNickname().contains(abusiveWord)) {
                return RegisterResult.FAILURE_ABUSE; // 닉네임에 욕설이 포함된 경우
            }
        }




        registerContactCode = this.registerMapper.selectRegisterContactCodeByContactSalt(registerContactCode);
        if (registerContactCode == null || !registerContactCode.isExpired()) {
            return RegisterResult.FAILURE;
        }
        user.setPassword(CryptoUtil.hashSha512(user.getPassword())); //저장되는 패스워드 암호화

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

            profile.setEmail(user.getEmail())
                    .setCreatedAt(new Date())
                    .setProfileThumbnail(defaultProfileImageBytes)
                    .setProfileThumbnailMime("image/png")// 이미지의 MIME 타입을 설정해야 합니다.
                    .setIntroduceText("마이페이지에서 수정해주세요")
                    .setNickname(user.getNickname());

        } catch (IOException e) {

        } //회원가입시 같이 프로필 추가되게

        return this.registerMapper.insertUser(user) > 0 && profileMapper.insertProfile(profile) > 0
                ? RegisterResult.SUCCESS
                : RegisterResult.FAILURE;
    }

    public CheckEmailResult checkEmailResult(String email) {
        return this.registerMapper.selectUserByEmail(email) == null
                ? CheckEmailResult.OKAY
                : CheckEmailResult.DUPLICATE;
    }

    public CheckNicknameResult checkNicknameResult(String nickname) {

        String[] abusiveWords = {"ㅅㅂ", "ㅆㅂ", "시발", "씨발", "10새", "10새기", "10새리", "10세리", "10쉐이", "10쉑", "10스", "10쌔",
                "10쌔기", "10쎄", "10알", "10창", "10탱", "18것", "18넘", "18년", "18노", "18놈",
                "18뇬", "18럼", "18롬", "18새", "18새끼", "18색", "18세끼", "18세리", "18섹", "18쉑", "18스", "18아",
                "c파", "c팔", "fuck", "ㄱㅐ", "ㄲㅏ", "ㄲㅑ", "ㄲㅣ", "ㅅㅂㄹㅁ", "ㅅㅐ", "ㅆㅂㄹㅁ", "ㅆㅍ", "ㅆㅣ", "ㅆ앙", "ㅍㅏ", "凸",
                "갈보", "갈보년", "강아지", "같은년", "같은뇬", "개같은", "개구라", "개년", "개놈",
                "개뇬", "개대중", "개독", "개돼중", "개랄", "개보지", "개뻥", "개뿔", "개새", "개새기", "개새끼",
                "개새키", "개색기", "개색끼", "개색키", "개색히", "개섀끼", "개세", "개세끼", "개세이", "개소리", "개쑈",
                "개쇳기", "개수작", "개쉐", "개쉐리", "개쉐이", "개쉑", "개쉽", "개스끼", "개시키", "개십새기",
                "개십새끼", "개쐑", "개씹", "개아들", "개자슥", "개자지", "개접", "개좆", "개좌식", "개허접", "걔새",
                "걔수작", "걔시끼", "걔시키", "걔썌", "걸레", "게색기", "게색끼", "광뇬", "구녕", "구라", "구멍",
                "그년", "그새끼", "냄비", "놈현", "뇬", "눈깔", "뉘미럴", "니귀미", "니기미", "니미", "니미랄", "니미럴",
                "니미씹", "니아배", "니아베", "니아비", "니어매", "니어메", "니어미", "닝기리", "닝기미", "대가리",
                "뎡신", "도라이", "돈놈", "돌아이", "돌은놈", "되질래", "뒈져", "뒈져라", "뒈진", "뒈진다", "뒈질",
                "뒤질래", "등신", "디져라", "디진다", "디질래", "딩시", "따식", "때놈", "또라이", "똘아이", "똘아이",
                "뙈놈", "뙤놈", "뙨넘", "뙨놈", "뚜쟁", "띠바", "띠발", "띠불", "띠팔", "메친넘", "메친놈", "미췬",
                "미췬", "미친", "미친넘", "미친년", "미친놈", "미친새끼", "미친스까이", "미틴", "미틴넘", "미틴년",
                "미틴놈", "바랄년", "병자", "뱅마", "뱅신", "벼엉신", "병쉰", "병신", "부랄", "부럴", "불알", "불할", "붕가",
                "붙어먹", "뷰웅", "븅", "븅신", "빌어먹", "빙시", "빙신", "빠가", "빠구리", "빠굴", "빠큐", "뻐큐",
                "뻑큐", "뽁큐", "상넘이", "상놈을", "상놈의", "상놈이", "새갸", "새꺄", "새끼", "새새끼", "새키",
                "색끼", "생쑈", "세갸", "세꺄", "세끼", "섹스", "쇼하네", "쉐", "쉐기", "쉐끼", "쉐리", "쉐에기",
                "쉐키", "쉑", "쉣", "쉨", "쉬발", "쉬밸", "쉬벌", "쉬뻘", "쉬펄", "쉽알", "스패킹", "스팽", "시궁창", "시끼",
                "시댕", "시뎅", "시랄", "시발", "시벌", "시부랄", "시부럴", "시부리", "시불", "시브랄", "시팍",
                "시팔", "시펄", "신발끈", "심발끈", "심탱", "십8", "십라", "십새", "십새끼", "십세", "십쉐", "십쉐이", "십스키",
                "십쌔", "십창", "십탱", "싶알", "싸가지", "싹아지", "쌉년", "쌍넘", "쌍년", "쌍놈", "쌍뇬", "쌔끼",
                "쌕", "쌩쑈", "쌴년", "썅", "썅년", "썅놈", "썡쇼", "써벌", "썩을년", "썩을놈", "쎄꺄", "쎄엑",
                "쒸벌", "쒸뻘", "쒸팔", "쒸펄", "쓰바", "쓰박", "쓰발", "쓰벌", "쓰팔", "씁새", "씁얼", "씌파", "씨8",
                "씨끼", "씨댕", "씨뎅", "씨바", "씨바랄", "씨박", "씨발", "씨방", "씨방새", "씨방세", "씨밸", "씨뱅",
                "씨벌", "씨벨", "씨봉", "씨봉알", "씨부랄", "씨부럴", "씨부렁", "씨부리", "씨불", "씨붕", "씨브랄",
                "씨빠", "씨빨", "씨뽀랄", "씨앙", "씨파", "씨팍", "씨팔", "씨펄", "씸년", "씸뇬", "씸새끼", "씹같", "씹년",
                "씹뇬", "씹보지", "씹새", "씹새기", "씹새끼", "씹새리", "씹세", "씹쉐", "씹스키", "씹쌔", "씹이", "씹자지",
                "씹질", "씹창", "씹탱", "씹퇭", "씹팔", "씹할", "씹헐", "아가리", "아갈", "아갈이", "아갈통",
                "아구창", "아구통", "아굴", "얌마", "양넘", "양년", "양놈", "엄창", "엠병", "여물통", "염병", "엿같", "옘병",
                "옘빙", "오입", "왜년", "왜놈", "욤병", "육갑", "은년", "을년", "이년", "이새끼", "이새키", "이스끼",
                "이스키", "임마", "자슥", "잡것", "잡넘", "잡년", "잡놈", "저년", "저새끼", "접년", "젖밥", "조까",
                "조까치", "조낸", "조또", "조랭", "조빠", "조쟁이", "조지냐", "조진다", "조찐", "조질래", "존나", "존나게", "존니", "존만",
                "존만한", "좀물", "좁년", "좁밥", "좃까", "좃또", "좃만", "좃밥", "좃이", "좃찐", "좆같", "좆까", "좆나",
                "좆또", "좆만", "좆밥", "좆이", "좆찐", "좇같", "좇이", "좌식", "주글", "주글래", "주데이", "주뎅",
                "주뎅이", "주둥아리", "주둥이", "주접", "주접떨", "죽고잡", "죽을래", "죽통", "쥐랄", "쥐롤",
                "쥬디", "지랄", "지럴", "지롤", "지미랄", "짜식", "짜아식", "쪼다", "쫍빱", "찌랄", "창녀", "캐년",
                "캐놈", "캐스끼", "캐스키", "캐시키", "탱구", "팔럼", "퍽큐", "호로", "호로놈", "호로새끼",
                "호로색", "호로쉑", "호로스까이", "호로스키", "후라들", "후래자식", "후레"}; // 욕설 단어들의 배열

        for (String abusiveWord : abusiveWords) {
            if (nickname.contains(abusiveWord)) {
                return CheckNicknameResult.ABUSE; // 욕설이 포함되어 있는 경우
            }
        }


        return this.registerMapper.selectUserByNickname(nickname) == null
                ? CheckNicknameResult.OKAY
                : CheckNicknameResult.DUPLICATE;
    }
}
