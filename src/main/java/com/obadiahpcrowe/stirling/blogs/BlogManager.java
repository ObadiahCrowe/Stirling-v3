package com.obadiahpcrowe.stirling.blogs;

import com.google.gson.Gson;
import com.obadiahpcrowe.stirling.accounts.StirlingAccount;
import com.obadiahpcrowe.stirling.accounts.enums.AccountType;
import com.obadiahpcrowe.stirling.blogs.obj.BlogPost;
import com.obadiahpcrowe.stirling.blogs.obj.StirlingBlog;
import com.obadiahpcrowe.stirling.database.DatabaseManager;
import com.obadiahpcrowe.stirling.database.obj.StirlingCall;
import com.obadiahpcrowe.stirling.resources.AttachableResource;
import com.obadiahpcrowe.stirling.util.msg.MsgTemplate;
import com.obadiahpcrowe.stirling.util.msg.StirlingMsg;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by: Obadiah Crowe (St1rling)
 * Creation Date / Time: 10/9/17 at 8:22 PM
 * Project: Stirling
 * Package: com.obadiahpcrowe.stirling.blogs
 * Copyright (c) Obadiah Crowe 2017
 */
public class BlogManager {

    private static BlogManager instance;
    private DatabaseManager databaseManager = DatabaseManager.getInstance();
    private Gson gson = new Gson();

    public String createBlog(StirlingAccount account, String title, String desc, AttachableResource bannerImage,
                             List<AccountType> targetAudience) {
        if (account.getAccountType().getAccessLevel() > 9) {
            if (!blogExists(title)) {
                databaseManager.makeCall(new StirlingCall(databaseManager.getBlogDB()).insert(
                  new StirlingBlog(account, title, desc, bannerImage, targetAudience)));
                return gson.toJson(new StirlingMsg(MsgTemplate.BLOG_CREATED, account.getLocale(), title));
            } else {
                return gson.toJson(new StirlingMsg(MsgTemplate.BLOG_ALREADY_EXISTS, account.getLocale(), title));
            }
        } else {
            return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(),
              "create a blog", AccountType.SUB_SCHOOL_LEADER.getFriendlyName()));
        }
    }

    public String editTargetAudience(StirlingAccount account, UUID blogUuid, List<AccountType> targetAudience) {
        if (account.getAccountType().getAccessLevel() > 9) {
            if (blogExists(blogUuid)) {
                StirlingBlog blog = getBlog(blogUuid);
                databaseManager.makeCall(new StirlingCall(databaseManager.getBlogDB()).replaceField(new HashMap<String, Object>() {{
                    put("uuid", blogUuid.toString());
                }}, "targetAudience", targetAudience));
                return gson.toJson(new StirlingMsg(MsgTemplate.BLOG_FIELD_EDITED, account.getLocale(),
                  "targetAudience", blog.getTitle()));
            } else {
                return gson.toJson(new StirlingMsg(MsgTemplate.BLOG_DOES_NOT_EXIST, account.getLocale(), blogUuid.toString()));
            }
        } else {
            return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(),
              "edit blog audiences", AccountType.SUB_SCHOOL_LEADER.getFriendlyName()));
        }
    }

    public String editTitle(StirlingAccount account, UUID blogUuid, String title) {
        if (account.getAccountType().getAccessLevel() > 9) {
            if (blogExists(blogUuid)) {
                StirlingBlog blog = getBlog(blogUuid);
                databaseManager.makeCall(new StirlingCall(databaseManager.getBlogDB()).replaceField(new HashMap<String, Object>() {{
                    put("uuid", blogUuid.toString());
                }}, "title", title));
                return gson.toJson(new StirlingMsg(MsgTemplate.BLOG_FIELD_EDITED, account.getLocale(),
                  "title", blog.getTitle()));
            } else {
                return gson.toJson(new StirlingMsg(MsgTemplate.BLOG_DOES_NOT_EXIST, account.getLocale(), blogUuid.toString()));
            }
        } else {
            return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(),
              "edit blog titles", AccountType.SUB_SCHOOL_LEADER.getFriendlyName()));
        }
    }

    public String editDesc(StirlingAccount account, UUID blogUuid, String desc) {
        if (account.getAccountType().getAccessLevel() > 9) {
            if (blogExists(blogUuid)) {
                StirlingBlog blog = getBlog(blogUuid);
                databaseManager.makeCall(new StirlingCall(databaseManager.getBlogDB()).replaceField(new HashMap<String, Object>() {{
                    put("uuid", blogUuid.toString());
                }}, "desc", desc));
                return gson.toJson(new StirlingMsg(MsgTemplate.BLOG_FIELD_EDITED, account.getLocale(),
                  "desc", blog.getTitle()));
            } else {
                return gson.toJson(new StirlingMsg(MsgTemplate.BLOG_DOES_NOT_EXIST, account.getLocale(), blogUuid.toString()));
            }
        } else {
            return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(),
              "edit blog descriptions", AccountType.SUB_SCHOOL_LEADER.getFriendlyName()));
        }
    }

    public String editBanner(StirlingAccount account, UUID blogUuid, AttachableResource bannerImage) {
        if (account.getAccountType().getAccessLevel() > 9) {
            if (blogExists(blogUuid)) {
                StirlingBlog blog = getBlog(blogUuid);
                databaseManager.makeCall(new StirlingCall(databaseManager.getBlogDB()).replaceField(new HashMap<String, Object>() {{
                    put("uuid", blogUuid.toString());
                }}, "bannerImage", bannerImage.getFile().getPath()));
                return gson.toJson(new StirlingMsg(MsgTemplate.BLOG_FIELD_EDITED, account.getLocale(),
                  "bannerImage", blog.getTitle()));
            } else {
                return gson.toJson(new StirlingMsg(MsgTemplate.BLOG_DOES_NOT_EXIST, account.getLocale(), blogUuid.toString()));
            }
        } else {
            return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(),
              "edit blog banner images", AccountType.SUB_SCHOOL_LEADER.getFriendlyName()));
        }
    }

    public String deleteBlog(StirlingAccount account, UUID blogUuid) {
        if (account.getAccountType().getAccessLevel() > 9) {
            if (blogExists(blogUuid)) {
                StirlingBlog blog = getBlog(blogUuid);
                databaseManager.makeCall(new StirlingCall(databaseManager.getBlogDB()).remove(new HashMap<String, Object>() {{
                    put("uuid", blogUuid.toString());
                }}));
                return gson.toJson(new StirlingMsg(MsgTemplate.BLOG_DELETED, account.getLocale(), blog.getTitle()));
            } else {
                return gson.toJson(new StirlingMsg(MsgTemplate.BLOG_DOES_NOT_EXIST, account.getLocale(), blogUuid.toString()));
            }
        } else {
            return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(),
              "delete a blog", AccountType.SUB_SCHOOL_LEADER.getFriendlyName()));
        }
    }

    public String createBlogPost(StirlingAccount account, UUID blogUuid, String title, String desc, String content,
                                 List<AttachableResource> resources) {
        if (account.getAccountType().getAccessLevel() > 9) {
            if (blogExists(blogUuid)) {
                BlogPost blogPost = new BlogPost(account, title, desc, content, resources);
                StirlingBlog blog = getBlog(blogUuid);

                blog.getBlogPosts().add(blogPost);
                databaseManager.makeCall(new StirlingCall(databaseManager.getBlogDB()).replaceField(new HashMap<String, Object>() {{
                    put("uuid", blog.getUuid());
                }}, "blogPosts", blog.getBlogPosts()));

                return gson.toJson(new StirlingMsg(MsgTemplate.BLOG_POST_CREATED, account.getLocale(), title, blog.getTitle()));
            } else {
                return gson.toJson(new StirlingMsg(MsgTemplate.BLOG_DOES_NOT_EXIST, account.getLocale(), blogUuid.toString()));
            }
        } else {
            return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(),
              "create blog posts", AccountType.SUB_SCHOOL_LEADER.getFriendlyName()));
        }
    }

    public String deleteBlogPost(StirlingAccount account, UUID blogUuid, UUID postUuid) {
        if (account.getAccountType().getAccessLevel() > 9) {
            if (blogExists(blogUuid)) {
                if (postExists(blogUuid, postUuid)) {
                    StirlingBlog blog = getBlog(blogUuid);
                    List<BlogPost> blogPosts = blog.getBlogPosts();
                    blogPosts.forEach(post -> {
                        if (post.getUuid().equals(postUuid)) {
                            blogPosts.remove(post);
                        }
                    });

                    databaseManager.makeCall(new StirlingCall(databaseManager.getBlogDB()).replaceField(new HashMap<String, Object>() {{
                        put("uuid", blogUuid.toString());
                    }}, "blogPosts", blogPosts));
                    return gson.toJson(new StirlingMsg(MsgTemplate.BLOG_POST_DELETED, account.getLocale(),
                      postUuid.toString(), blog.getTitle()));
                } else {
                    return gson.toJson(new StirlingMsg(MsgTemplate.BLOG_POST_DOES_NOT_EXIST, account.getLocale(), postUuid.toString()));
                }
            } else {
                return gson.toJson(new StirlingMsg(MsgTemplate.BLOG_DOES_NOT_EXIST, account.getLocale(), blogUuid.toString()));
            }
        } else {
            return gson.toJson(new StirlingMsg(MsgTemplate.INSUFFICIENT_PERMISSIONS, account.getLocale(),
              "delete blog posts", AccountType.SUB_SCHOOL_LEADER.getFriendlyName()));
        }
    }

    public String editBlogTitle(StirlingAccount account, UUID blogUuid, UUID postUuid, String title) {
        if (account.getAccountType().getAccessLevel() > 9) {
            if (blogExists(blogUuid)) {
                if (postExists(blogUuid, postUuid)) {
                    StirlingBlog blog = getBlog(blogUuid);
                    BlogPost post = blog.getBlogPosts().stream().filter(blogPost -> blogPost.getUuid().equals(postUuid)).findFirst().get();

                    System.out.println(post.getTitle());

                    databaseManager.makeCall(new StirlingCall(databaseManager.getBlogDB()).replaceField(new HashMap<String, Object>() {{
                        put("uuid", blogUuid.toString());
                    }}, "blogPOsts", blog.getBlogPosts()));
                }
            }
        }
        return "test";
    }

    public StirlingBlog getBlog(UUID uuid) {
        if (blogExists(uuid)) {
            return (StirlingBlog) databaseManager.makeCall(new StirlingCall(databaseManager.getBlogDB()).get(new HashMap<String, Object>() {{
                put("uuid", uuid.toString());
            }}, StirlingBlog.class));
        }
        return null;
    }

    private boolean blogExists(String blogName) {
        try {
            StirlingBlog blog = (StirlingBlog) databaseManager.makeCall(new StirlingCall(databaseManager.getBlogDB()).get(new HashMap<String, Object>() {{
                put("title", blogName);
            }}, StirlingBlog.class));

            if (blog != null) {
                return true;
            }
        } catch (NullPointerException e) { }
        return false;
    }

    private boolean blogExists(UUID uuid) {
        try {
            StirlingBlog blog = (StirlingBlog) databaseManager.makeCall(new StirlingCall(databaseManager.getBlogDB()).get(new HashMap<String, Object>() {{
                put("uuid", uuid.toString());
            }}, StirlingBlog.class));

            if (blog != null) {
                return true;
            }
        } catch (NullPointerException ignored) { }
        return false;
    }

    private boolean postExists(UUID blogUuid, UUID postUuid) {
        List<BlogPost> blogPosts = getBlog(blogUuid).getBlogPosts();
        for (BlogPost post : blogPosts) {
            if (post.getUuid().equals(postUuid)) {
                return true;
            }
        }
        return false;
    }

    public static BlogManager getInstance() {
        if (instance == null)
            instance = new BlogManager();
        return instance;
    }
}
