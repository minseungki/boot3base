package com.example.demo.service;

import com.example.demo.dto.bbs.*;
import com.example.demo.dto.common.EPageInfo;
import com.example.demo.dto.common.RedisCacheKey;
import com.example.demo.dto.common.enumeration.CustomErrorCode;
import com.example.demo.exception.NotfoundException;
import com.example.demo.mapper.BbsMapper;
import com.example.demo.util.MapperUtil;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BbsService {

    @Getter @AllArgsConstructor
    public enum BbsERRCd implements CustomErrorCode {
        ERR_BBS_001("게시판 목록 조회 실패"),
        ERR_BBS_002("게시판 Page 조회 실패"),
        ERR_BBS_003("게시판 상세 조회 실패"),
        ERR_BBS_004("게시판 등록 실패"),
        ERR_BBS_005("게시판 수정 실패"),
        ERR_BBS_006("게시판 삭제 실패"),
		;
		private final String desc;
	}

    private final BbsMapper mapper;

    public List<BbsCategoryCdSelectListResponse> list() {
        List<BbsCategoryCdSelectListResponse> results = new ArrayList<>();
        results.add(BbsCategoryCdSelectListResponse.builder().code("NOTICE").label("공지사항").build());
        results.add(BbsCategoryCdSelectListResponse.builder().code("FAQ").label("FAQ").build());
        return results;
    }

    @Cacheable(value = RedisCacheKey.BBS, cacheManager = "cacheManager1Days", keyGenerator = "redisCacheKeyGenerator")
    public EPageInfo<BbsSelectPageResponse> page(BbsSelectPageRequest req) {
        PageHelper.startPage(req.getCurrentPage(), 10);
        /*
		 	// 기간검색 1개 일 경우

		 	ex)
		 	1. 요청 객체 DateRequest 상속
		 	public class BbsSelectPageRequest {
		 		...
		 	}

		 	->

		 	public class BbsSelectPageRequest extends DateRequest {
		 		...
		 	}

		 	2. 검색 시작일 + "00:00:00", 검색 종료일 + "23:59:59" 셋팅
		 	MapperUtil.setDateRequest(req);

		 	3. Mapper.xml 쿼리 queryBeginDt, queryEndDt 사용
		 	<select>
		 		SELECT
		 			....
		 		FROM
		 			....
		 		WHERE
		 			REGISTRATION_DT >= #{queryBeginDt}
		 			AND REGISTRATION_DT <= #{queryEndDt}
		 	</select>


		 	// 파일이 있는 경우
			EPageInfo<BbsSelectPageResponse> res = new EPageInfo<>(mapper.selectBbsPage(req));

			for (BbsSelectPageResponse bbsSelectPageResponse : res.getList()) {
				bbsSelectPageResponse.setFileList(commonFileService.getFileList(true, FileTargetCd.bbs, req.getBbsSeq()));
			}
			return res;

			// return type이 Page + Object 일 경우
			public class BbsSelectPageResponse {
				@ApiModelProperty(value = "배너 목록", position = 1)
				private List<FrontCampaignSelectBannerListResponse> bannerList;

				@ApiModelProperty(value = "캠페인 목록", position = 2)
				private EPageInfo<FrontCampaignSelectPageListResponse> campaignList;
		 	}

			// 조회 기능 분리
		 	public class Service {

		 		public BbsSelectPageResponse page(BbsSelectPageRequest req) {
					BbsSelectPageResponse res = new BbsSelectPageResponse();
					res.setBannerList(selectBanner(req));
					res.setCampaignList(selectCampaignPage(req));
		 		}

		 		private List<FrontCampaignSelectBannerListResponse> selectBanner(BbsSelectPageRequest req) {
		 			....
				}

		 		private EPageInfo<FrontCampaignSelectPageListResponse> selectCampaignPage(BbsSelectPageRequest req) {
		 			....
				}
		 	}

		 */
        return new EPageInfo<>(mapper.selectBbsPage(req));
    }

    public BbsSelectResponse detail(BbsSelectRequest req) {
        BbsSelectResponse response = mapper.selectBbs(req);

        if (null == response) {
            throw new NotfoundException();
        }

		/*
			// 파일이 있는 경우
			if (null == response) {
				throw new NotfoundException();
			} else {
				response.setFileList(commonFileService.getFileList(true, FileTargetCd.bbs, req.getBbsSeq()));
			}
		 */
        return response;
    }

    @Transactional
    public void insert(BbsInsertRequest req) {

		/*
			// 서비스 단 에러처리
			if (dto.getTitle().length() == 1) {
				throw new CustomException(BbsERRCd.ERR_BBS_001.toString(), BbsERRCd.ERR_BBS_001.getDesc());
			}
		 */
        MapperUtil.setBaseRequest(req); // BaseRequest 셋팅
        mapper.insertBbs(req); // 등록처리

		/*
			// 등록 후 등록된 일련번호로 추가 작업이 필요할 때
			Long bbsSeq = req.getBbsSeq();

			// 파일 업로드가 있는 경우
			List<FileUploadDto> fileList = req.getFileList();
			commonFileService.insertFileList(true, FileTargetCd.bbs, fileList.toArray(new FileUploadDto[fileList.size()-1]) , bbsSeq, Integer.class);
		 */
    }

    @Transactional
    public void update(BbsUpdateRequest req) {
        MapperUtil.setBaseRequest(req); // BaseRequest 셋팅

		/*
			// 파일 업로드가 있는 경우
			Long bbsSeq = req.getBbsSeq();
			List<FileUploadDto> fileList = req.getFileList();
			commonFileService.insertFileList(true, FileTargetCd.bbs, fileList.toArray(new FileUploadDto[fileList.size()-1]) , bbsSeq, Integer.class);
		 */

        mapper.updateBbs(req);
    }

    @Transactional
    public void delete(BbsDeleteRequest req) {
        MapperUtil.setBaseRequest(req);
        mapper.deleteBbs(req);
    }

}
