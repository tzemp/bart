package ch.uzh.zempt.stackoverflow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Representation of a StackExchangeQuestion
 */
public class StackExchangeQuestion {

    /**
     * The answer count.
     */
    private int answerCount;

    /**
     * The question id.
     */
    private Long questionId;

    /**
     * The creation date.
     */
    private Date creationDate;

    /**
     * The last edit date.
     */
    private Date lastEditDate;

    /**
     * The last activity date.
     */
    private Date lastActivityDate;

    /**
     * The up vote count.
     */
    private long upVoteCount;

    /**
     * The down vote count.
     */
    private long downVoteCount;

    /**
     * The favorite count.
     */
    private long favoriteCount;

    /**
     * The view count.
     */
    private long viewCount;

    /**
     * The score.
     */
    private long score;

    /**
     * The community owned.
     */
    private boolean communityOwned;

    /**
     * The title.
     */
    private String title;

    /**
     * The tags.
     */
    private List<String> tags = new ArrayList<>();

    /**
     * The accepted answer id.
     */
    private long acceptedAnswerId;

    /**
     * The body.
     */
    private String body;

    /**
     * The answers.
     */
    private List<StackExchangeAnswer> answers = new ArrayList<>();

    /**
     * The closed date.
     */
    private Date closedDate;

    /**
     * The closed reason.
     */
    private String closedReason;

    /**
     * The locked date.
     */
    private Date lockedDate;

    /**
     * The bounty closes date.
     */
    private Date bountyClosesDate;

    /**
     * The bounty amount.
     */
    private long bountyAmount;

    /**
     * The question timeline url.
     */
    private String questionTimelineUrl;

    /**
     * The question comments url.
     */
    private String questionCommentsUrl;

    /**
     * The question answers url.
     */
    private String questionAnswersUrl;

    /**
     * The question url.
     */
    private String link;

    /**
     * The question is answered.
     */
    private boolean isAnswered;

    private Date protectedDate;

    public int getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(int answerCount) {
        this.answerCount = answerCount;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public long getAcceptedAnswerId() {
        return acceptedAnswerId;
    }

    public void setAcceptedAnswerId(long acceptedAnswerId) {
        this.acceptedAnswerId = acceptedAnswerId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getClosedDate() {
        return closedDate;
    }

    public void setClosedDate(Date closedDate) {
        this.closedDate = closedDate;
    }

    public String getClosedReason() {
        return closedReason;
    }

    public void setClosedReason(String closedReason) {
        this.closedReason = closedReason;
    }

    public Date getLockedDate() {
        return lockedDate;
    }

    public void setLockedDate(Date lockedDate) {
        this.lockedDate = lockedDate;
    }

    public Date getBountyClosesDate() {
        return bountyClosesDate;
    }

    public void setBountyClosesDate(Date bountyClosesDate) {
        this.bountyClosesDate = bountyClosesDate;
    }

    public long getBountyAmount() {
        return bountyAmount;
    }

    public void setBountyAmount(long bountyAmount) {
        this.bountyAmount = bountyAmount;
    }

    public String getQuestionTimelineUrl() {
        return questionTimelineUrl;
    }

    public void setQuestionTimelineUrl(String questionTimelineUrl) {
        this.questionTimelineUrl = questionTimelineUrl;
    }

    public String getQuestionCommentsUrl() {
        return questionCommentsUrl;
    }

    public void setQuestionCommentsUrl(String questionCommentsUrl) {
        this.questionCommentsUrl = questionCommentsUrl;
    }

    public String getQuestionAnswersUrl() {
        return questionAnswersUrl;
    }

    public void setQuestionAnswersUrl(String questionAnswersUrl) {
        this.questionAnswersUrl = questionAnswersUrl;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isAnswered() {
        return isAnswered;
    }

    public void setAnswered(boolean answered) {
        isAnswered = answered;
    }

    public Date getProtectedDate() {
        return protectedDate;
    }

    public void setProtectedDate(Date protectedDate) {
        this.protectedDate = protectedDate;
    }

    public List<StackExchangeAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<StackExchangeAnswer> answers) {
        this.answers = answers;
    }

    public StackExchangeAnswer getAnswerByAnswerId(Long answerId) {
        for (StackExchangeAnswer stackExchangeAnswer : this.getAnswers()) {
            if (stackExchangeAnswer.getAnswerId() == answerId) {
                return stackExchangeAnswer;
            }
        }
        return null;
    }

    public StackExchangeAnswer getAnswerWithMostVotes() {
        long answerId = 0L;
        long voteCount = 0;
        for (StackExchangeAnswer answer : this.getAnswers()) {
            if (voteCount >= answer.getUpVoteCount()) {
                voteCount = answer.getUpVoteCount();
                answerId = answer.getAnswerId();
            }
        }
        return this.getAnswerByAnswerId(answerId);
    }

    public StackExchangeAnswer getBestAnswer() {
        if (this.getAcceptedAnswerId() > 0) {
            return this.getAnswerByAnswerId(this.getAcceptedAnswerId());
        } else {
            return this.getAnswerWithMostVotes();
        }
    }
}
