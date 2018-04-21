package cn.yyp.srs.model;


/**
 * 动态评论实体
 */
public class DynamicComment {

    private String commenterId; //评论者id
    private String commenterName; //评论者名字
    private String content; //评论内容
    private String time; //评论时间

    public DynamicComment(){

    }

    public DynamicComment(String commenterId, String commenterName, String content, String time) {
        this.commenterId = commenterId;
        this.commenterName = commenterName;
        this.content = content;
        this.time = time;
    }

    public String getCommenterId() {
        return commenterId;
    }

    public void setCommenterId(String commenterId) {
        this.commenterId = commenterId;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public void setCommenterName(String commenterName) {
        this.commenterName = commenterName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
