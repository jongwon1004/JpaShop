package jpashop.jpabook.jpashop.api;

import jakarta.validation.Valid;
import jpashop.jpabook.jpashop.domain.Member;
import jpashop.jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {

        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result membersV2() {

        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(member -> new MemberDto(member.getName()))
                .collect(Collectors.toList());

        return new Result(collect.size(), collect);

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }


    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) { // 엔티티 자체를 파라미터로 받는것은 위험 (Api 스펙 변경 등) Dto를 통해서 받자
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }


    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {

        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id, @RequestBody @Valid UpdateMemberRequest request) {

        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);

        return new UpdateMemberResponse(id, findMember.getName());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class CreateMemberRequest {
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class CreateMemberResponse {
        private Long id;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class UpdateMemberRequest {
        private String name;
    }
}
