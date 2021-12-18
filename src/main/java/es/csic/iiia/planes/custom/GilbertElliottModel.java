package es.csic.iiia.planes.custom;

public class GilbertElliottModel {

    public boolean state;
    public double p_gg;
    public double p_bb;

    public GilbertElliottModel(double p_gg, double p_bb) {
        state = true; // Initially in good state
        this.p_gg = p_gg;
        this.p_bb = p_bb;
    }

    public boolean isReceived() {
        if(state) {
            // Check if transitions to bad state
            if(BernoulliModel.bernoulli(p_gg) == false) {
                // If yes, set state to bad state
                state = false;
            }
        }
        else {
            // Check if transitions to good state
            if(BernoulliModel.bernoulli(p_bb) == false) {
                // If yes, set state to bad state
                state = true;
            }
            // If yes, set state to good state
        }
        return state;
    }
}