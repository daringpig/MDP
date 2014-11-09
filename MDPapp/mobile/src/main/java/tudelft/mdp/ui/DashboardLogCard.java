package tudelft.mdp.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import tudelft.mdp.R;

public class DashboardLogCard extends Card {

    private Context context;

    public DashboardLogCard(Context context) {
        super(context, R.layout.card_dashboard_log);
        this.context = context;
        init();
    }

    public DashboardLogCard(Context context, int innerLayout) {
        super(context, innerLayout);
        this.context = context;
    }

    private void init(){
        CardHeader header = new LocatorHeader(context, "Log");
        this.addCardHeader(header);
        this.setShadow(true);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

    }

}