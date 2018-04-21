package cn.yyp.srs.model.i;

import cn.yyp.srs.bean.User;

/**
 * 评论接口.
 */
public interface OnDynamicCommentListener {
    void onComment(User user, String dynamicId);
}
