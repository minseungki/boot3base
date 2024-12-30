package com.example.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MaskingUtil {
    public static final char DEFAULT_REPLACE = '*';

    public static void main(String[] args) {

        MaskingUtil makingUtil = new MaskingUtil();
        log.info(makingUtil.getName("홍길"));
        log.info(makingUtil.getName("홍길동"));
        log.info(makingUtil.getName("홍길동1"));
        log.info(makingUtil.getName("홍길동12"));
        log.info(makingUtil.getName("홍길동123"));
        log.info(makingUtil.getName("honggil dong"));
        log.info(makingUtil.getTelephone("01012345678"));
        log.info(makingUtil.getTelephone("0101234567"));
        log.info(makingUtil.getEmail("emotion@emotion.co.kr"));
        log.info(makingUtil.getEmail("emotion1@emotion.co.kr"));
    }

    /**
     * 마스킹 이름
     *
     * @param name
     * @return
     */
    public String getName(String name) {
        if (name.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")) {//한글
            int len = name.length();
            int makingCnt = len - 2;
            if (makingCnt == 0) {
                makingCnt = 1;
            }
            return masking(name, 1, makingCnt);
        } else {//영문
            int maskingCnt = 4;
            return masking(name, maskingCnt);
        }
    }

    /**
     * 마스킹 전화번호
     *
     * @param telephone
     * @return
     */
    public String getTelephone(String telephone) {
        // 마스킹할 번호가 존재해야 하므로
        if (telephone != null && !"".equals(telephone)) {
            int cnt = telephone.length();
            if (cnt == 10) {
                telephone = telephone.substring(0, 3) + "***"
                        + telephone.substring(telephone.length() - 4);
            } else {
                telephone = telephone.substring(0, 3) + "****"
                        + telephone.substring(telephone.length() - 4);
            }
        }
        return telephone;
    }

    /**
     * 마스킹 이메일
     *
     * @param email
     * @return
     */
    public String getEmail(String email) {
        String maskingEmail = "";
        if (email.indexOf("@") > 0) {
            String mailFirst = email.split("@")[0];
            String mailLast = email.split("@")[1];
            int emailLen = mailFirst.length();
            int emailLen2 = emailLen / 2;
            maskingEmail = masking(mailFirst, emailLen2, emailLen);
            maskingEmail += "@" + mailLast;
        } else {
            maskingEmail = email;
        }
        return maskingEmail;
    }

    /**
     * 문자열 마스킹
     *
     * @param src      원본
     * @param startIdx 시작위치
     * @return 마스킹 적용된 문자열
     */
    public static String masking(String src, int startIdx) {
        return masking(src, DEFAULT_REPLACE, null, startIdx, src.length());
    }

    /**
     * 문자열 마스킹
     *
     * @param src      원본
     * @param startIdx 시작위치
     * @param length   길이
     * @return 마스킹 적용된 문자열
     */
    public static String masking(String src, int startIdx, int length) {
        return masking(src, DEFAULT_REPLACE, null, startIdx, length);
    }

    /**
     * 문자열 마스킹
     *
     * @param src      원본
     * @param replace  대치문자
     * @param startIdx 시작위치
     * @return 마스킹 적용된 문자열
     */
    public static String masking(String src, char replace, int startIdx) {
        return masking(src, replace, null, startIdx, src.length());
    }

    /**
     * 문자열 마스킹
     *
     * @param src      원본
     * @param replace  대치문자
     * @param startIdx 시작위치
     * @param length   길이
     * @return 마스킹 적용된 문자열
     */
    public static String masking(String src, char replace, int startIdx,
                                 int length) {
        return masking(src, replace, null, startIdx, length);
    }

    /**
     * 문자열 마스킹
     *
     * @param src      원본
     * @param replace  대치문자
     * @param exclude  제외문자
     * @param startIdx 시작위치
     * @param length   길이
     * @return 마스킹 적용된 문자열
     */
    public static String masking(String src, char replace, char[] exclude,
                                 int startIdx, int length) {
        StringBuffer sb = new StringBuffer(src);

        // 종료 인덱스
        int endIdx = startIdx + length;
        if (sb.length() < endIdx)
            endIdx = sb.length();

        // 치환
        for (int i = startIdx; i < endIdx; i++) {
            boolean isExclude = false;
            // 제외 문자처리
            if (exclude != null && 0 < exclude.length) {
                char currentChar = sb.charAt(i);

                for (char excludeChar : exclude) {
                    if (currentChar == excludeChar) {
                        isExclude = true;
                    }
                }
            }

            if (!isExclude) {
                sb.setCharAt(i, replace);
            }
        }

        return sb.toString();
    }

}

