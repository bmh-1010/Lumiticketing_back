package com.care.boot.member;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MemberController {
    @Autowired private MemberService service;  // ✅ 중복 제거
    @Autowired private HttpSession session;
    @Autowired private KakaoService kakaoService; // ✅ KakaoService 추가

    @RequestMapping("regist")
    public String regist() {
        return "member/regist";
    }

    @PostMapping("registProc")
    public String registProc(MemberDTO member, Model model, RedirectAttributes ra) {
        System.out.println("회원가입 요청 받음: " + member.toString());
        String msg = service.registProc(member);
        System.out.println("회원가입 처리 결과: " + msg);

        if (msg.equals("회원 등록 완료")) {
            ra.addFlashAttribute("msg", msg);
            return "redirect:index";
        }
        else if(msg.equals("이미 사용중인 아이디입니다.")) {
        	 ra.addFlashAttribute("msg", msg);
             return "redirect:index";
        }
        return "index";
        
       
        
        
    }

    @RequestMapping("login")
    public String login() {
        return "member/login";
    }

    @PostMapping("loginProc")
    public String loginProc(String id, String pw, Model model, RedirectAttributes ra) {
        String msg = service.loginProc(id, pw);
        if(msg.equals("로그인 성공")) {
            ra.addFlashAttribute("msg", msg);
            return "redirect:index";
        }
        model.addAttribute("msg", msg);
        return "member/login";
    }

    @RequestMapping("logout")
    public String logout(RedirectAttributes ra, HttpSession session) {
        session.invalidate(); // 세션 무효화 (로그아웃)
        
        // 로그아웃 메시지를 Flash Attribute로 저장
        ra.addFlashAttribute("logoutMessage", "로그아웃되었습니다!");

        // 홈(index.jsp)으로 이동
        return "redirect:login.lumiticketing.click";
    }

    @RequestMapping("vipPayment")
    public String vipPayment() {
        return "member/vipPayment";
    }
    
    @RequestMapping("ticketingPayment")
    public String ticketingPayment() {
        return "member/ticketingPayment";
    }
    
    @PostMapping("ticketingPaymentProc")
    public String ticketingPaymentProc(HttpSession session, RedirectAttributes redirect, Model model) {
        String id = (String) session.getAttribute("id");

        // ✅ 로그인 확인
        if (id == null) {
            redirect.addFlashAttribute("msg", "로그인 후 이용해주세요!");
            return "redirect:/login"; // 로그인 페이지로 이동
        }

        boolean success = memberService.reserveTicket(id); // ✅ 티켓 예매 로직 실행

        // ✅ 예매 결과 메시지 저장 및 리다이렉트
        if (success) {
            redirect.addFlashAttribute("msg", "🎉 예매 성공!");
        } else {
            redirect.addFlashAttribute("msg", "❌ 예매 실패!");
        }

        return "redirect:/ticketing"; // ✅ 티켓 예매 결과 후 다시 티켓 페이지로 이동
    }



    @PostMapping("vipPaymentProc")
    public String vipPaymentProc(RedirectAttributes ra) {
        String sessionId = (String) session.getAttribute("id");
        if (sessionId == null) {
            ra.addFlashAttribute("msg", "로그인이 필요합니다.");
            return "redirect:login.lumiticketing.click";
        }

        String msg = service.upgradeToVIP(sessionId);
        if (msg.equals("VIP 승격 완료!")) {
            session.invalidate();
            ra.addFlashAttribute("vipUpgradeMessage", "🎉 VIP로 승격되었습니다!");  // ✅ 🔥 Flash Attribute 추가
            return "redirect:index";
        }

        // VIP 승격 실패 시 다시 VIP 결제 페이지로 이동
        ra.addFlashAttribute("msg", "VIP 승격 실패!");;
        return "member/vipPayment";
       }

    @PostMapping("/upgradeToVIP")
    public ResponseEntity<String> upgradeToVIP(@RequestParam String memberId) {
        try {
            service.upgradeMemberToVIP(memberId);
            return ResponseEntity.ok("VIP 승격 완료!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("VIP 승격 실패!");
        }
    }
    
    @RequestMapping("memberInfoVIP")
    public String memberInfoVIP(@RequestParam(required = false) String keyword, Model model) {
        List<MemberDTO> members;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            members = service.searchVipMembers(keyword);
        } else {
            members = service.getVipMembers(); // 검색어 없으면 전체 조회
        }

        model.addAttribute("members", members);
        return "member/memberInfoVIP";
    }

    
    @RequestMapping("ticketing")
    public String ticketing() {
        return "member/ticketing";  // ✅ JSP 파일과 일치해야 함
    }

    @RequestMapping("ticketHolder")
    public String ticketHolder(@RequestParam(required = false) String keyword, Model model) {
        List<MemberDTO> tickets;
        if (keyword != null && !keyword.trim().isEmpty()) {
            tickets = service.searchTicketHolders(keyword);
        } else {
            tickets = service.getAllTicketHolders();
        }
        model.addAttribute("tickets", tickets);
        return "member/ticketHolder";
    }

    
    @Autowired
    private MemberService memberService; // ✅ 인스턴스 변수로 선언
    
    @PostMapping("/reserveTicket")
    public String reserveTicket(HttpSession session, RedirectAttributes redirect, Model model) {
        String id = (String) session.getAttribute("id");

        // ✅ 로그인 확인
        if (id == null) {
            redirect.addFlashAttribute("msg", "로그인 후 이용해주세요!");
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }

        

        return "member/ticketingPayment"; // ✅ ticketingPayment.jsp 페이지로 이동
    }

    @RequestMapping("memberInfoRegular")
    public String memberInfoRegular(@RequestParam(required = false) String keyword, Model model) {
        List<MemberDTO> members;
        if (keyword != null && !keyword.trim().isEmpty()) {
            members = service.searchRegularMembers(keyword); // ✅ 인자 추가
        } else {
            members = service.getRegularMembers();
        }
        model.addAttribute("members", members);
        return "member/memberInfoRegular";
    }
    
    @PostMapping("/promoteToVIP")
    public ResponseEntity<String> promoteToVIP(@RequestParam String id) {
        System.out.println("🔹 등업 요청 - 회원 ID: " + id);

        try {
            service.upgradeMemberToVIP(id);
            return ResponseEntity.ok("VIP 승격 완료!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("VIP 승격 실패!");
        }
    }
    
    @PostMapping("/downgradetoRegular")
    public ResponseEntity<String> downgradeToRegular(@RequestParam String id) {
        System.out.println("🔹 다운그레이드 요청 - 회원 ID: " + id);

        try {
            service.downgradeMemberToRegular(id);
            return ResponseEntity.ok("일반회원 전환 완료!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("일반회원 전환 실패!");
        }
    }





}
