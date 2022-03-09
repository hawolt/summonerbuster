package com.hawolt.core.data;

import com.hawolt.core.model.ParticipantWrapper;

/**
 * Created: 04/03/2022 14:03
 * Author: Twitter @hawolt
 **/

public class Player {

    private final ParticipantWrapper wrapper;
    private final Participant participant;

    public Player(Participant participant, ParticipantWrapper wrapper) {
        this.participant = participant;
        this.wrapper = wrapper;
    }

    public ParticipantWrapper getWrapper() {
        return wrapper;
    }

    public Participant getParticipant() {
        return participant;
    }
}
