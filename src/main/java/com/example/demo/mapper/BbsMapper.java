package com.example.demo.mapper;

import com.example.demo.dto.bbs.*;
import com.example.demo.dto.common.RedisCacheKey;
import com.github.pagehelper.Page;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;

@Repository
public interface BbsMapper {
    
	Page<BbsSelectPageResponse> selectBbsPage(BbsSelectPageRequest req);
    BbsSelectResponse selectBbs(BbsSelectRequest req);
    @CacheEvict(value = RedisCacheKey.BBS_DEL, allEntries = true, cacheManager = "cacheManager")
    Long insertBbs(BbsInsertRequest req);
    @CacheEvict(value = RedisCacheKey.BBS_DEL, allEntries = true, cacheManager = "cacheManager")
    Long updateBbs(BbsUpdateRequest req);
    @CacheEvict(value = RedisCacheKey.BBS_DEL, allEntries = true, cacheManager = "cacheManager")
    Long deleteBbs(BbsDeleteRequest req);
    
}
