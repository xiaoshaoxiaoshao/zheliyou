package edu.zjff.shzj.util;

public interface Constant {
    String BASE="http://shzj.pwnhub.net/";
    String BASE1="http://shzj-server.pwnhub.net:8997/";//"http://shzj-test2.pwnhub.net/";"http://shzj.pwnhub.net/";//
    String BROADCAST_REG_SUCCESS = "BROADCAST_REG_SUCCESS";
    String BROADCAST_REG_WRONG = "BROADCAST_REG_WRONG";
    String REGIST_URL = BASE1+"android/register";
    String YZM_URL = BASE1+"captcha/captchaImage?type=char";
    String GETARTICLELIST_URL=BASE1+"android/articleList";
    String GetArticle_URL=BASE1+"android/queryArticle";
    String USER = "USER";
    String YZM = "YZM";
    String GETIMG_URL=BASE+"images/";
    String GETHENDIMG_URL=BASE+"images/avatar/";
    String Comment_URL=BASE1+"android/queryComments";
    String video_Comment_URL=BASE1+"android/queryVideoComments";
    String BROADCAST_YZM_WRONG = "BROADCAST_YZM_WRONG";
    String GETQINIUTOKEN = BASE1+"android/getQiniuToken";
    String BROADCAST_YZM_SUCCESS = "BROADCAST_YZM_SUCCESS";
    String MESSAGE = "MESSAGE";
    String BROADCAST_LOGIN_WRONG = "BROADCAST_LOGIN_WRONG";
    String BROADCAST_LOGIN_SUCCESS = "BROADCAST_LOGIN_SUCCESS";
    String LOGIN_URL = BASE1+"android/login";
    String BROADCAST_GETARTICLE_SUCCESS = "BROADCAST_GETARTICLE_SUCCESS";
    String BROADCAST_GETARTICLE_WRONG = "BROADCAST_GETARTICLE_WRONG";
    String INSERTNewComment=BASE1+"android/insertNewComment";
    String VIDEO_INSERTNewComment=BASE1+"android/insertNewVideoComment";
    String DELETECOMMENT=BASE1+"android/deleteComment";
    String VIDEO_DELETECOMMENT=BASE1+"android/deleteVideoComment";
    int REQUEST_USER = 100;
    String GETAllCATEGORY = BASE1+"android/getAllArticleCategory";
    String INSERTNewArticle=BASE1+"android/insertNewArticle";
    String INSERTNewVideo=BASE1+"android/insertNewVideo";

    String VIDEOLIST = BASE1+"android/videoList";
    String ZAN = BASE1+"android/articleLike";
    String VIDEO_LIKE= BASE1+"android/videoLike";
}
