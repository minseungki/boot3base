package com.example.demo.mapper;

import com.example.demo.dto.member.MemberInsertRequest;
import com.example.demo.dto.member.MemberSelectResponse;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberMapper {

    MemberSelectResponse selectDuplicateMember(MemberInsertRequest req);

    int insertMember(MemberInsertRequest req);

}
