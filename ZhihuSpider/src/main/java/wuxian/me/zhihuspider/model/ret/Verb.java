package wuxian.me.zhihuspider.model.ret;

/**
 * Created by wuxian on 1/9/2017.
 */
public class Verb {

    public Author author;
    public Question question;

    public String id;

    public Integer voteup_count;
    public Integer thanks_count;
    public Integer answer_count;
    public Integer follower_count;
    public Integer articles_count;
    public Integer followers;
    public Integer comment_count;

    public String title;
    public String name;
    public String content;  //Fixme:这里有content了啊 似乎不用post另一个PraiseAnswerSpider了 --> 这样子就需要使用LDA主题生成模型了

    public static class Question {
        public String title;
        //若是知乎专栏里的文章 那么--> https://zhuanlan.zhihu.com/p/28880958 它是没有topic的 因此需要进行分类
        public Long id; //从这里拿到问题的id

        public String type; //"question","article"
        public Integer answer_count;
        public Long created;
        public Integer follower_count;
        public Boolean is_following;
    }

    public static class Author {
        public String url_token;  //https://www.zhihu.com/people/divinites/answers

        public Boolean is_followd;
        public Boolean is_org;
        public String headline;
        public Integer gender;
        public String name;
        public String type;  //"people"
        //id为"0"说明是匿名用户 需要过滤掉
        public String id; //不知道这个是干嘛用的 https://api.zhihu.com/people/06916ab5292b620917a7be1b4945a19d
    }
}
