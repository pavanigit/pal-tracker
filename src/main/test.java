package com.nowhere.but.here;

import java.time.LocalDateTime;
import java.util.Optional;

import com.nowhere.but.here.enums.MotionResult;
import com.nowhere.but.here.enums.Vote;
import com.nowhere.but.here.exception.AlreadyVotedException;
import com.nowhere.but.here.exception.MotionDidNotStartException;
import com.nowhere.but.here.exception.MotionNameIsNotSameExceptoin;
import com.nowhere.but.here.exception.MotionReachedMaxVoteExcepction;
import com.nowhere.but.here.exception.TooEarlyMotionClosureException;
import com.nowhere.but.here.exception.VpNotAllowedToVoteException;
import com.nowhere.but.here.motion.Motion;
import com.nowhere.but.here.senate.Senator;
import com.nowhere.but.here.senate.SenatorVotingDetails;
import com.nowhere.but.here.senate.VicePresident;

public class RunMeApplication {

    public static final int MAX_VOTES = 101;

    // TODO: Need to change it to 15 minutes, set to 1 minute for testing
    public static final int MINIMUM_VOTING_PERIOD_MINS = 1;

    private Motion motion;

    public Motion getMotion() {
        if (this.motion == null) {
            this.motion = new Motion();
            this.motion.setMotionName(new String());
        }
        return motion;
    }

    public void setMotion(Motion motion) {
        this.motion = motion;
    }

    public int getMotionCurrentForCount() {
        return getMotion().getForVotes();
    }

    public int getMotionCurrentAgainstCount() {
        return getMotion().getAgainstVotes();
    }


    /**
     * Start Motion
     *
     * @param motionName
     */
    public void startMotion(String motionName) {
        getMotion().setInMotion(true);
        getMotion().setMotionName(motionName);
        getMotion().setStartTime(LocalDateTime.now());
    }


    /**
     * Cast Senator Vote
     *
     * AlreadyVotedException is thrown if Senator already cast his vote and trying to vote again
     * MotionReachedMaxVoteExcepction is thrown if Max votes is reached
     * MotionDidNotStartException is thrown if Senator try to vote when motion has not started.
     * MotionNameIsNotSameExceptoin is thrown if Senator try to vote for different motion
     *
     * @param senator
     * @throws AlreadyVotedException
     * @throws MotionReachedMaxVoteExcepction
     * @throws MotionDidNotStartException
     * @throws MotionNameIsNotSameExceptoin
     */
    public void castSenatorVote(Senator senator)
            throws AlreadyVotedException, MotionReachedMaxVoteExcepction, MotionDidNotStartException, MotionNameIsNotSameExceptoin {
        checkVotingEligibility(senator);
        castVote(senator);
    }


    /**
     * Cast VicePresidentVote Vote
     *
     * VpNotAllowedToVoteException is thrown when VP is not available to vote
     *
     * @param vicePresident
     * @throws VpNotAllowedToVoteException
     */
    public void castVicePresidentVote(VicePresident vicePresident) throws VpNotAllowedToVoteException {

        // Check if VP Available to Vote
        if (vicePresident.isAvailability()) {
            castVoteVP(vicePresident);
            getMotionResultAfterVPVote();
        }
        else {
            getMotionResultVPNotAvailableToVote();
        }
    }


    /**
     *  End Motion
     *
     *  TooEarlyMotionClosureException is thrown when we try to close motion before closure limit
     *
     *  @param motionName
     *  @throws TooEarlyMotionClosureException
     */
    public void endMotion(String motionName) throws TooEarlyMotionClosureException {

        // Check if we are ending correct motion
        if (getMotion().getMotionName().equals(motionName)) {
            checkEndMotionClosure();
            getMotionResult();
        }
    }


    /**
     *  Check Motion end to early
     *
     *  TooEarlyMotionClosureException is thrown when we try to close motion before closure limit
     *
     *  @param motionName
     *  @throws TooEarlyMotionClosureException
     */
    public void motionEndToEarly(String motionName) throws TooEarlyMotionClosureException {

        // Check if we are ending correct motion
        if (getMotion().getMotionName().equals(motionName))
            checkEndMotionClosure();
    }


    /**
     * Check the Eligibility if Voting can be done
     *
     * AlreadyVotedException is thrown if Senator already cast his vote and trying to vote again
     * MotionReachedMaxVoteExcepction is thrown if Max votes is reached
     * MotionDidNotStartException is thrown if Senator try to vote when motion has not started.
     * MotionNameIsNotSameExceptoin is thrown if Senator try to vote for different motion
     *
     * @param senator
     * @throws AlreadyVotedException
     * @throws MotionReachedMaxVoteExcepction
     * @throws MotionDidNotStartException
     * @throws MotionNameIsNotSameExceptoin
     */
    private void checkVotingEligibility(Senator senator)
            throws AlreadyVotedException, MotionReachedMaxVoteExcepction, MotionDidNotStartException, MotionNameIsNotSameExceptoin {
        // checking motion started
        if( getMotion().isInMotion()) {

            // Check motion name match OR Check YEAS + NAYS <= 101 OR Check senator already voted
            if (!checkSenatorVotingToCorrectMotion(senator))
                throw new MotionNameIsNotSameExceptoin();
            else if (getMotion().getForVotes() + getMotion().getAgainstVotes() == MAX_VOTES)
                throw new MotionReachedMaxVoteExcepction();
            else if (checkSenatorAlreadyVotedMotion(senator))
                throw new AlreadyVotedException();
        }
        else
        {
            // Vote cannot be cast when motion is not started
            throw new MotionDidNotStartException();
        }
    }


    private boolean checkSenatorVotingToCorrectMotion(Senator senator) {

        boolean result = false;
        Optional<SenatorVotingDetails> SenatorVotingDetailsOptional = Optional.ofNullable(getSenatorVotingDetails(senator));

        if(SenatorVotingDetailsOptional.isPresent())
            result = true;

        return result;
    }

    private boolean checkSenatorAlreadyVotedMotion(Senator senator) {

        boolean result = false;
        Optional<SenatorVotingDetails> SenatorVotingDetailsOptional = Optional.ofNullable(getSenatorVotingDetails(senator));

        if(SenatorVotingDetailsOptional.isPresent()) {
            result = SenatorVotingDetailsOptional.get().isAlreadyVoted();
        }

        return result;
    }


    private SenatorVotingDetails getSenatorVotingDetails (Senator senator) {

        SenatorVotingDetails senatorVotingDetails = senator.getSenatorVotingDetails()
                .stream()
                .filter((s) -> getMotion().getMotionName().equals(s.getMotionName()))
                .findAny()
                .orElse(null);
        return senatorVotingDetails;
    }


    /**
     * Senator Vote is cast and Count is updated
     *
     * @param senator
     */
    private void castVote(Senator senator) {
        // Check if Vote.YEAH, then increment motion's For Count, else Against Count
        Optional<SenatorVotingDetails> SenatorVotingDetailsOptional = Optional.ofNullable(getSenatorVotingDetails(senator));
        if(SenatorVotingDetailsOptional.isPresent()) {
            SenatorVotingDetails senatorVotingDetails_ = SenatorVotingDetailsOptional.get();

            if (senatorVotingDetails_.getVote().equals(Vote.YEAS))
                getMotion().setForVotes(getMotionCurrentForCount() + 1);
            else if (senatorVotingDetails_.getVote().equals(Vote.NAYS))
                getMotion().setAgainstVotes(getMotionCurrentAgainstCount() + 1);

            // Set flag to already voted
            senatorVotingDetails_.setAlreadyVoted(true);
        }
    }


    /**
     * VicePresident Vote is cast and Count is updated
     *
     * VpNotAllowedToVoteException is thrown if VP tries to Vote when motion is not TIE
     *
     * @param senator
     * @throws VpNotAllowedToVoteException
     */
    private void castVoteVP(VicePresident vicePresident) throws VpNotAllowedToVoteException{

        // Check if motion is tied
        if ( getMotion().getMotionResult().equals(MotionResult.TIE)) {

            // Check motion name match
            Optional<SenatorVotingDetails> VpVotingDetailsOptional = Optional.ofNullable(getSenatorVotingDetails(vicePresident));
            if(VpVotingDetailsOptional.isPresent()) {
                SenatorVotingDetails vpVotingDetails_ = VpVotingDetailsOptional.get();

                if (vpVotingDetails_.getVote().equals(Vote.YEAS))
                    getMotion().setForVotes(getMotionCurrentForCount() + 1);
                else if (vpVotingDetails_.getVote().equals(Vote.NAYS))
                    getMotion().setAgainstVotes(getMotionCurrentAgainstCount() + 1);

                // Set flag to already voted
                vpVotingDetails_.setAlreadyVoted(true);
            }
        }
        else {
            throw new VpNotAllowedToVoteException();
        }

    }


    /**
     * Check if Motion can be closed
     *
     * TooEarlyMotionClosureException is thrown if we try to close motion before minimum voting period
     * set motion end time
     *
     * @throws TooEarlyMotionClosureException
     */
    private void checkEndMotionClosure() throws TooEarlyMotionClosureException {
        LocalDateTime currentTime = LocalDateTime.now();

        if ( currentTime.isEqual(getMotion().getStartTime().plusMinutes(MINIMUM_VOTING_PERIOD_MINS))
                || currentTime.isAfter(getMotion().getStartTime().plusMinutes(MINIMUM_VOTING_PERIOD_MINS)) ) {

            // Capture motion endTime
            getMotion().setEndTime(currentTime);
        }
        else {
            throw new TooEarlyMotionClosureException();
        }
    }


    /**
     * Get Motion result
     *
     */
    private void getMotionResult() {
        if (getMotion().getForVotes() > getMotion().getAgainstVotes()) {
            getMotion().setMotionResult(MotionResult.PASS);
            getMotion().setInMotion(false);
        }else if (getMotion().getForVotes() < getMotion().getAgainstVotes()) {
            getMotion().setMotionResult(MotionResult.FAIL);
            getMotion().setInMotion(false);
        }else if (getMotion().getForVotes() == getMotion().getAgainstVotes()) {
            getMotion().setMotionResult(MotionResult.TIE);
        }
    }


    /**
     * Get Motion result after VicePresident Votes in case of TIE scenario
     *
     * set motion to close
     * set motion new end time
     *
     */
    private void getMotionResultAfterVPVote() {
        LocalDateTime currentTime = LocalDateTime.now();

        if (getMotion().getForVotes() > getMotion().getAgainstVotes()) {
            getMotion().setMotionResult(MotionResult.PASS);
        }else if (getMotion().getForVotes() < getMotion().getAgainstVotes()) {
            getMotion().setMotionResult(MotionResult.FAIL);
        }

        // Set motion closed and capture motion end time after VP votes
        getMotion().setInMotion(false);
        getMotion().setEndTime(currentTime);
    }


    /**
     * Get Motion result if VicePresident Not available to vote
     *
     * set motion to Fail
     * set motion to close
     * set motion new end time
     *
     */
    private void getMotionResultVPNotAvailableToVote() {
        LocalDateTime currentTime = LocalDateTime.now();

        // Set motion failed & closed and capture motion end time
        getMotion().setMotionResult(MotionResult.FAIL);
        getMotion().setInMotion(false);
        getMotion().setEndTime(currentTime);
    }

}
