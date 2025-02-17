package model.board;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BoardDAO {
    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;

    private void getConnection() {
        try {
            Context con = new InitialContext();
            Context envCon = (Context) con.lookup("java:comp/env");
            DataSource ds = (DataSource) envCon.lookup("jdbc/mydb");// 이부분 중요
            conn = ds.getConnection();
        }catch (Exception e ){
            e.printStackTrace();
        }
    }

    // 게시글 작성
    public boolean createBoard(BoardBean boardBean){
        getConnection();
        boolean chk = false;
        try{
            String sql = "insert into board values (no,?,?,?,now(),?,0,1,1)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,boardBean.getTitle());
            pstmt.setString(2,boardBean.getAuthor());
            pstmt.setString(3,boardBean.getPw());
            pstmt.setString(4,boardBean.getContents());
            pstmt.executeUpdate();
            System.out.println(boardBean);
            conn.close();
            chk = true;
        }catch (Exception e ){
            e.printStackTrace();
        }
        return chk;
    }

    // 게시글 개수 리턴
    public int boardCnt() {
        getConnection();
        int cnt = 0;
        try{
            String sql = "select count(*) from board";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if(rs.next()){
                cnt = rs.getInt(1);
            }
            conn.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return cnt;
    }

    //검색된 게시글 조회
    public List<BoardBean> searchBoard(String keyword) {
        List<BoardBean> selectBoardList = new ArrayList<>();
        getConnection();

        try{
            String sql = "select * from board where title like '%"+keyword+"%' or contents like '%"+keyword+"%'";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                BoardBean boardBean = new BoardBean();
                boardBean.setNo(rs.getInt(1));
                boardBean.setTitle(rs.getString(2));
                boardBean.setAuthor(rs.getString(3));
                boardBean.setPw(rs.getString(4));
                boardBean.setDate(rs.getString(5));
                boardBean.setContents(rs.getString(6));
                boardBean.setReadcnt(rs.getInt(7));
                boardBean.setRef_step(rs.getInt(8));
                boardBean.setRef_step(rs.getInt(9));
                selectBoardList.add(boardBean);
            }
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return selectBoardList;
    }

    //게시글 전체 조회(최신글순)
    public List<BoardBean> showAll() {
        List<BoardBean> boardBeanList = new ArrayList<>();
        getConnection();

        try {
            String sql = "select * from board order by no desc";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()){
                BoardBean boardBean = new BoardBean();
                boardBean.setNo(rs.getInt(1));
                boardBean.setTitle(rs.getString(2));
                boardBean.setAuthor(rs.getString(3));
                boardBean.setPw(rs.getString(4));
                boardBean.setDate(rs.getString(5));
                boardBean.setContents(rs.getString(6));
                boardBean.setReadcnt(rs.getInt(7));
                boardBean.setRef_step(rs.getInt(8));
                boardBean.setRef_step(rs.getInt(9));
                boardBeanList.add(boardBean);
            }
            conn.close();
        } catch (Exception e ){
            e.printStackTrace();
        }

        return boardBeanList;
    }

    //세부 게시물 조회
    public BoardBean getContents(int boardNo) {
        BoardBean bean = new BoardBean();
        getConnection();
        try{
            //조회수 증가
            String sqlcnt = "update board set readcnt=readcnt+1 where no = ?";
            pstmt = conn.prepareStatement(sqlcnt);
            pstmt.setInt(1,boardNo);
            pstmt.executeUpdate();

            String sql = "select * from board where no = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,boardNo);
            rs = pstmt.executeQuery();
            if(rs.next()){
                bean.setNo(boardNo);
                bean.setTitle((rs.getString(2)));
                bean.setAuthor(rs.getString(3));
                bean.setPw(rs.getString(4));
                bean.setDate(rs.getString(5));
                bean.setContents(rs.getString(6));
                bean.setReadcnt(rs.getInt(7));
                bean.setRef(rs.getInt(8));
                bean.setRef_step(rs.getInt(9));
            }
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return bean;
    }

    //댓글작성
    public void insertReply(BoardBean bean) {
        getConnection();

        try{
            String sql = "insert into reply values(no,?,?,?,?,now())";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,bean.getRef());
            pstmt.setString(2, bean.getAuthor());
            pstmt.setString(3,bean.getPw());
            pstmt.setString(4,bean.getContents());
            pstmt.executeUpdate();
            conn.close();
        }catch (Exception e ){
            e.printStackTrace();
        }
    }

    // 해당 게시글에 맞는 댓글 리턴
    public ArrayList<BoardBean> getReply(int no) {
        ArrayList<BoardBean> reply = new ArrayList<>();
        getConnection();
        try{
            String sql = "select * from reply where ref = ? order by no asc";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,no);
            rs = pstmt.executeQuery();
            while(rs.next()){
                BoardBean bean = new BoardBean();
                bean.setNo(rs.getInt(1));
                bean.setAuthor(rs.getString(3));
                bean.setContents(rs.getString(5));
                bean.setDate(rs.getString(6));
                reply.add(bean);
            }
            conn.close();
        }catch (Exception e ){
            e.printStackTrace();
        }
        return reply;
    }
    //게시글  수정 삭제
    public void updateBoard(int no, String content ,int type){
        getConnection();
        try{
            String sql = "";
            if (type == 1){
                sql = "update board set contents = ? where no = ?";
            }
            else {
                sql = "update reply set content = ? where no = ?";
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,content);
            pstmt.setInt(2, no);
            pstmt.executeUpdate();
            System.out.println(no+"번 글 수정 완료");
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void deleteBoard(int no, int type){
        getConnection();
        try{
            String sql = "";
            if (type == 1){
                sql = "delete from board where no = ?";
            }
            else {
                sql = "delete from reply where no = ?";
            }

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, no);
            pstmt.executeUpdate();
            System.out.println(no+"번 글/댓글 삭제 완료 ");
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    //게시글 삭제시 게시글 관련 댓글 삭제를 위한 ref값 리턴
    public void deleteAllRefReply(int boardNo){
        getConnection();
        try{
            String sql = "delete from reply where ref = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,boardNo);
            pstmt.executeUpdate();
            System.out.println(boardNo +"번 글 관련 댓글 삭제");
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //게시글, 댓글 수정시 비밀번호 체크
    public boolean checkPwd(int No, String pwd, int type){
        boolean check = false;
        getConnection();
        try{
            String sql = "";
            if (type == 1){
                sql = "select pw from board where no = ?";
            }
            else{
                sql = "select pw from reply where no = ?";
            }

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,No);
            rs = pstmt.executeQuery();
            if (rs.next()){
                if (rs.getString(1).equals(pwd)){
                    check = true;
                    System.out.println("패스워드 같음");
                }
            }
            conn.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return check;
    }
}
