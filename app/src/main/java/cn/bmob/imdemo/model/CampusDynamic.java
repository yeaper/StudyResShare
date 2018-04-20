package cn.bmob.imdemo.model;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * 动态实体
 */
public class CampusDynamic extends BmobObject {

    private String authorId; //作者id
    private String authorName; //作者名字
    private String content; //动态内容
    private List<DynamicComment> commentList; //评论列表

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public List<DynamicComment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<DynamicComment> commentList) {
        this.commentList = commentList;
    }
}
