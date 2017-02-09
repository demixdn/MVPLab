package com.github.mvplab.data.db;

/**
 * Date: 06.02.2017
 * Time: 14:58
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

class DatabaseConst {
    static final class DATABASE {
        static final String NAME = "posts_db.db";
        static final int VERSION = 1;
    }

    static final class TABLE {
        static final String POSTS = "Posts";
        static final String USERS = "Users";
        static final String COMMENTS = "Comments";
    }

    static final class USER_FIELDS {
        static final String ID = "id";
        static final String NAME = "name";
        static final String USERNAME = "username";
        static final String EMAIL = "email";
    }

    static final class POST_FIELDS {
        static final String ID = "id";
        static final String USER_ID = "user_id";
        static final String TITLE = "title";
        static final String BODY = "body";
    }

    static final class COMMENT_FIELDS {
        static final String POST_ID = "post_id";
        static final String ID = "id";
        static final String NAME = "name";
        static final String EMAIL = "email";
        static final String BODY = "body";
    }

    static final class QUERY {

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
