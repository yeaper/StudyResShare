package cn.bmob.imdemo.model.i;

import cn.bmob.imdemo.bean.User;

/**
 * 评论接口.
 */
public interface OnDynamicCommentListener {
    void onComment(User user, String dynamicId);
}
