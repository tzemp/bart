package ch.uzh.zempt.stackoverflow;

import java.util.Date;

public class StackExchangeAnswer {

    /** The answer id. */
    private long answerId;

    /** The accepted. */
    private boolean accepted;

    /** The question id. */
    private long questionId;

    /** The creation date. */
    private Date creationDate;

    /** The last edit date. */
    private Date lastEditDate;

    /** The last activity date. */
    private Date lastActivityDate;

    /** The up vote count. */
    private long upVoteCount;

    /** The down vote count. */
    private long downVoteCount;

    /** The favorite count. */
    private long favoriteCount;

    /** The view count. */
    private long viewCount;

    /** The score. */
    private long score;

    /** The community owned. */
    private boolean communityOwned;

    /** The title. */
    private String title;

    /** The body. */
    private String body;

    /** The answer comments url. */
    private String answerCommentsUrl;

    private Date lockedDate;

    public long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(long answerId) {
        this.answerId = answerId;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastEditDate() {
        return lastEditDate;
    }

    public void setLastEditDate(Date lastEditDate) {
        this.lastEditDate = lastEditDate;
    }

    public Date getLastActivityDate() {
        return lastActivityDate;
    }

    public void setLastActivityDate(Date lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }

    public long getUpVoteCount() {
        return upVoteCount;
    }

    public void setUpVoteCount(long upVoteCount) {
        this.upVoteCount = upVoteCount;
    }

    public long getDownVoteCount() {
        return downVoteCount;
    }

    public void setDownVoteCount(long downVoteCount) {
        this.downVoteCount = downVoteCount;
    }

    public long getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(long favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public boolean isCommunityOwned() {
        return communityOwned;
    }

    public void setCommunityOwned(boolean communityOwned) {
        this.communityOwned = communityOwned;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAnswerCommentsUrl() {
        return answerCommentsUrl;
    }

    public void setAnswerCommentsUrl(String answerCommentsUrl) {
        this.answerCommentsUrl = answerCommentsUrl;
    }

    public Date getLockedDate() {
        return lockedDate;
    }

    public void setLockedDate(Date lockedDate) {
        this.lockedDate = lockedDate;
    }
}
