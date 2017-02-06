package com.github.mvplab.db;

/**
 * Date: 06.02.2017
 * Time: 14:58
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

public class DatabaseConst {
    public static final class DATABASE {
        public static final String NAME = "posts_db.db";
        public static final int VERSION = 1;
    }

    public static final class TABLE {
        public static final String POSTS = "Posts";
        public static final String USERS = "Users";
        public static final String COMMENTS = "Comments";
    }

    public static final class USER_FIELDS {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String USERNAME = "username";
        public static final String EMAIL = "email";
    }

    public static final class POST_FIELDS {
        public static final String ID = "id";
        public static final String USER_ID = "user_id";
        public static final String TITLE = "title";
        public static final String BODY = "body";
    }

    public static final class COMMENT_FIELDS {
        public static final String POST_ID = "post_id";
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String EMAIL = "email";
        public static final String BODY = "body";
    }

    public static final class QUERY {

        static final String CREATE_TABLE_USERS = "CREATE TABLE "
                + TABLE.USERS
                + " ("
                + USER_FIELDS.ID + " INTEGER PRIMARY KEY, "
                + USER_FIELDS.NAME + " TEXT, "
                + USER_FIELDS.EMAIL + " TEXT, "
                + USER_FIELDS.USERNAME + " TEXT "
                + ");";

        static final String CREATE_TABLE_POSTS = "CREATE TABLE "
                + TABLE.POSTS
                + " ("
                + POST_FIELDS.ID + " INTEGER PRIMARY KEY, "
                + POST_FIELDS.USER_ID + " INT, "
                + POST_FIELDS.TITLE + " TEXT, "
                + POST_FIELDS.BODY + " TEXT "
                + ");";

        static final String CREATE_TABLE_COMMENTS = "CREATE TABLE "
                + TABLE.COMMENTS
                + " ("
                + COMMENT_FIELDS.ID + " INTEGER PRIMARY KEY, "
                + COMMENT_FIELDS.POST_ID + " INT, "
                + COMMENT_FIELDS.NAME + " TEXT, "
                + COMMENT_FIELDS.EMAIL + " TEXT, "
                + COMMENT_FIELDS.BODY + " TEXT "
                + ");";

        static final String DROP_TABLE_USERS = "DROP TABLE IF EXISTS " + TABLE.USERS + ";";
        static final String DROP_TABLE_POSTS = "DROP TABLE IF EXISTS " + TABLE.POSTS + ";";
        static final String DROP_TABLE_COMMENTS = "DROP TABLE IF EXISTS " + TABLE.COMMENTS + ";";
    }
}
