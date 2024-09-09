package wad.Wad.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;
import wad.Wad.Repository.RefreshRepository;

import java.io.IOException;

public class CustomLogoutFilter extends GenericFilterBean {

    private final RefreshRepository refreshRepository;

    public CustomLogoutFilter(RefreshRepository refreshRepository) {
        this.refreshRepository = refreshRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // path and method verify
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout$")) {
            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        // get refresh token from cookie
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        // Remove refresh token from DB if it exists
        if (refreshToken != null && refreshRepository.existsByRefresh(refreshToken)) {
            refreshRepository.deleteByRefresh(refreshToken);
            System.out.println("Refresh token deleted from DB");
        }

        // 쿠키를 정리 (특히 refresh 토큰 제거)
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);  // 쿠키 삭제
        cookie.setPath("/");

        response.addCookie(cookie);  // 쿠키 제거 응답에 추가
        response.setStatus(HttpServletResponse.SC_OK);  // 로그아웃 성공 상태
        response.getWriter().write("Logout successful");
        response.getWriter().flush();
        System.out.println("Logout successful");
    }
}
