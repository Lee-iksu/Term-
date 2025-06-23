package presenter;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

import model.Message;
import view.main.MainView;

public class MainPresenter {
    // 메인 화면을 구성하는 뷰 객체
    private final MainView view;

    // 로그인한 사용자 ID
    private final String userId;

    // 서버로 메시지를 전송하기 위한 출력 스트림
    private final PrintWriter out;

    // 서버와의 연결을 유지하는 소켓 객체
    private final Socket socket;

    // 생성자: 뷰, 사용자 ID, 서버 스트림 및 소켓을 주입받아 초기화
    public MainPresenter(MainView view, String userId, PrintWriter out, Socket socket) {
        this.view = view;
        this.userId = userId;
        this.out = out;
        this.socket = socket;
    }

    // 로그아웃 요청을 처리하는 메서드
    public void logout() {
        try {
            Gson gson = new Gson();

            // 로그아웃 메시지를 구성 (메시지 타입: logout, 대상: all)
            Message logoutMsg = new Message(userId, "", "logout", "all", "", null, 0);

            // 메시지를 JSON 형식으로 서버에 전송
            out.println(gson.toJson(logoutMsg));
            out.flush();

            // 서버에서 로그아웃을 정상 처리할 시간을 확보하기 위해 일시 정지
            Thread.sleep(300);
        } catch (Exception ex) {
            // 예외 발생 시 로그 출력
            ex.printStackTrace();
        } finally {
            try {
                // 소켓을 안전하게 종료
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // 로그아웃 종료 로그 출력 (개발자용 메시지)
            System.out.println("로그아웃 메시지 전송 후 종료");
        }
    }

    // 친구 탭을 클릭했을 때 호출되는 메서드
    public void onFriendTabClicked() {
        view.showFriendPanel();  // 친구 목록 패널을 뷰에 표시
    }

    // 채팅 탭을 클릭했을 때 호출되는 메서드
    public void onChatTabClicked() {
        view.showChatPanel();    // 채팅 화면 패널을 뷰에 표시
    }

    // 일정 탭을 클릭했을 때 호출되는 메서드
    public void onScheduleTabClicked() {
        view.showSchedulePanel();  // 일정 관리 패널을 뷰에 표시
    }

    // 프로필 탭을 클릭했을 때 호출되는 메서드
    public void onProfileTabClicked() {
        view.showProfilePanel();   // 사용자 프로필 패널을 뷰에 표시
    }
}
