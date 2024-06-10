package com.example.mindle.API;

public class Post {
    public int id;
    public String title;
    public int author_id;
    public String text_content;
    public int likes;
    public int dislikes;
    public int comments;
    public Post(int _id, String _title, int _author_id,String _text_content,int _likes,int _dislikes,int _comments){
        this.id = _id;
        this.title = _title;
        this.author_id = _author_id;
        this.text_content = _text_content;
        this.likes = _likes;
        this.dislikes = _dislikes;
        this.comments = _comments;
    }

}
