package com.smartjob.common;

public class Constants {
    
    public static final String DEFAULT_AVATAR = "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20portrait%20avatar&image_size=square";
    public static final String DEFAULT_COMPANY_LOGO = "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=company%20logo%20icon&image_size=square";
    
    public static final Integer USER_STATUS_NORMAL = 1;
    public static final Integer USER_STATUS_DISABLED = 2;
    
    public static final String ROLE_JOBSEEKER = "jobseeker";
    public static final String ROLE_HR = "hr";
    public static final String ROLE_ADMIN = "admin";
    
    public static final Integer JOB_STATUS_DRAFT = 0;
    public static final Integer JOB_STATUS_ONLINE = 1;
    public static final Integer JOB_STATUS_OFFLINE = 2;
    
    public static final Integer AUDIT_STATUS_PENDING = 2;
    public static final Integer AUDIT_STATUS_APPROVED = 1;
    public static final Integer AUDIT_STATUS_REJECTED = 3;
    
    public static final Integer APPLICATION_STATUS_PENDING = 1;
    public static final Integer APPLICATION_STATUS_INTERVIEW_PENDING = 2;
    public static final Integer APPLICATION_STATUS_INTERVIEWING = 3;
    public static final Integer APPLICATION_STATUS_OFFER_PENDING = 4;
    public static final Integer APPLICATION_STATUS_REJECTED = 5;
    public static final Integer APPLICATION_STATUS_HIRED = 6;
    
    public static final Integer ANNOUNCEMENT_STATUS_PUBLISHED = 1;
    public static final Integer ANNOUNCEMENT_STATUS_DRAFT = 2;
    
    public static final Integer ANNOUNCEMENT_TYPE_SYSTEM = 1;
    public static final Integer ANNOUNCEMENT_TYPE_ACTIVITY = 2;
    public static final Integer ANNOUNCEMENT_TYPE_OTHER = 3;
    
    public static final String[] CITIES = {"北京", "上海", "广州", "深圳", "杭州", "成都", "武汉", "西安", "南京", "重庆", "苏州", "天津"};
    
    public static final String[] EDUCATIONS = {"大专", "本科", "硕士", "博士"};
    
    public static final String[] WORK_YEARS = {"应届毕业生", "1-3年", "3-5年", "5-10年", "10年以上"};
    
    public static final String[] JOB_TYPES = {"全职", "兼职", "实习"};
}
