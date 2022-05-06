package com.main.dhbworld;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.main.dhbworld.Enums.InteractionState;

import com.main.dhbworld.Firebase.CurrentStatusListener;
import com.main.dhbworld.Firebase.DataSendListener;
import com.main.dhbworld.Firebase.ReportCountListener;
import com.main.dhbworld.Firebase.SignedInListener;
import com.main.dhbworld.Firebase.Utilities;
import com.main.dhbworld.Fragments.DialogCofirmationUserInteraction;
import com.main.dhbworld.Navigation.NavigationUtilities;

import java.util.ArrayList;

public class UserInteraction extends AppCompatActivity {
    private ArrayList <Button> yes;
    private ArrayList <Button> no;

    private TextView stateCanteenTV;
    private TextView stateCoffeeTV;
    private TextView statePrinterTV;
    private TextView notificationCanteenTV;
    private TextView notificationCoffeeTV;
    private TextView notificationPrinterTV;

    private InteractionState stateCanteen;
    private InteractionState stateCoffee;
    private static InteractionState statePrinter;

    private long notificationCanteen;
    private long notificationCoffee;
    private long notificationPrinter;

    private LinearLayout imageBox_canteen;
    private LinearLayout imageBox_coffee;
    private LinearLayout imageBox_printer;

    private Utilities firebaseUtilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_interaction_layout);

        NavigationUtilities.setUpNavigation(this, R.id.UserInteraction);

        stateManagement();
        yesNoButtonsManagement();
        notificationManagement();

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Utilities.subscribeToTopics();

        firebaseUtilities = new Utilities(this);
        firebaseUtilities.setSignedInListener(new SignedInListener() {
            @Override
            public void onSignedIn(FirebaseUser user) {
                progressDialog.dismiss();
            }

            @Override
            public void onSignInError() {
                progressDialog.dismiss();
                Toast.makeText(UserInteraction.this, "Fehler", Toast.LENGTH_SHORT).show();
            }
        });
        firebaseUtilities.setCurrentStatusListener(new CurrentStatusListener() {
            @Override
            public void onStatusReceived(String category, int status) {
                Resources res = getResources();
                switch (category) {
                    case Utilities.CATEGORY_CAFETERIA:
                        System.out.println("STATUS: " + status);
                        stateCanteen = InteractionState.parseId(status);
                        stateCanteenTV.setText(getResources().getString(R.string.state) + " " + stateCanteen.getText());
                        imageBox_canteen.setBackgroundColor(res.getColor(stateCanteen.getColor()));
                        break;
                    case Utilities.CATEGORY_COFFEE:
                        stateCoffee = InteractionState.parseId(status);
                        stateCoffeeTV.setText(getResources().getString(R.string.state) + " " + stateCoffee.getText());
                        imageBox_coffee.setBackgroundColor(res.getColor(stateCoffee.getColor()));
                        break;
                    case Utilities.CATEGORY_PRINTER:
                        statePrinter = InteractionState.parseId(status);
                        statePrinterTV.setText(getResources().getString(R.string.state) + " " + statePrinter.getText());
                        imageBox_printer.setBackgroundColor(res.getColor(statePrinter.getColor()));
                        break;
                }
            }
        });

        firebaseUtilities.setReportCountListener(new ReportCountListener() {
            @Override
            public void onReportCountReceived(String category, long reportCount) {
                switch (category) {
                    case Utilities.CATEGORY_CAFETERIA:
                        notificationCanteen = reportCount;
                        notificationCanteenTV.setText(getResources().getString(R.string.previous_notifications) + " " + notificationCanteen);
                        break;
                    case Utilities.CATEGORY_COFFEE:
                        notificationCoffee = reportCount;
                        notificationCoffeeTV.setText(getResources().getString(R.string.previous_notifications) + " " + notificationCoffee);
                        break;
                    case Utilities.CATEGORY_PRINTER:
                        notificationPrinter = reportCount;
                        notificationPrinterTV.setText(getResources().getString(R.string.previous_notifications) + " " + notificationPrinter);
                        break;
                }
            }
        });

        firebaseUtilities.signIn();

        updateInteractionState();

    }

    private void yesNoButtonsManagement(){
        yes =new ArrayList<> ();
        yes.add(findViewById(R.id.yes0));
        yes.add(findViewById(R.id.yes1));
        yes.add(findViewById(R.id.yes2));


        no =new ArrayList<> ();
        no.add(findViewById(R.id.no0));
        no.add(findViewById(R.id.no1));
        no.add(findViewById(R.id.no2));

        InteractionState[][] states= new InteractionState[3][];
        states[0]= new InteractionState[]{InteractionState.QUEUE_SHORT, InteractionState.QUEUE_MIDDLE, InteractionState.QUEUE_LONG};
        states[1]= new InteractionState[]{InteractionState.CLEANING, InteractionState.DEFECT};
        //states[2]= new String[]{"", ""};

        for(Button j:yes){
            if (j.equals(yes.get(2))) {
                continue;
            }
            j.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DialogCofirmationUserInteraction confirmation= new DialogCofirmationUserInteraction(UserInteraction.this, states[yes.indexOf(j)] , R.string.problem_report);
                    confirmation.setPositiveButton(R.string.problem_report, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            firebaseUtilities.setDataSendListener(new DataSendListener() {
                                @Override
                                public void success() {
                                    Toast.makeText(UserInteraction.this, R.string.thank_you, Toast.LENGTH_SHORT).show();
                                    updateInteractionState();
                                }

                                @Override
                                public void failed(Exception reason) {
                                    Toast.makeText(UserInteraction.this, reason.getMessage(), Toast.LENGTH_LONG).show();
                                    Toast.makeText(UserInteraction.this, "Fehler", Toast.LENGTH_SHORT).show();
                                }
                            });

                            String category = "";
                            if (j.equals(yes.get(0))) {
                                category = Utilities.CATEGORY_CAFETERIA;
                            } else if (j.equals(yes.get(1))) {
                                category = Utilities.CATEGORY_COFFEE;
                            }
                            firebaseUtilities.addToDatabase(category, confirmation.getSelectedState().getId());
                        }
                    });
                    confirmation.show();
                }});

        }

        yes.get(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogCofirmationUserInteraction confirmation= new DialogCofirmationUserInteraction(UserInteraction.this, R.string.moechten_sie_melden_dass_der_drucker_kaputt_ist, R.string.problem_report);

                confirmation.setPositiveButton(R.string.problem_report, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        firebaseUtilities.setDataSendListener(new DataSendListener() {
                            @Override
                            public void success() {
                                Toast.makeText(UserInteraction.this, R.string.thank_you, Toast.LENGTH_SHORT).show();
                                updateInteractionState();
                            }

                            @Override
                            public void failed(Exception reason) {
                                Toast.makeText(UserInteraction.this, "Fehler", Toast.LENGTH_SHORT).show();
                            }
                        });

                        String category = Utilities.CATEGORY_PRINTER;
                        firebaseUtilities.addToDatabase(category, InteractionState.DEFECT.getId());
                    }
                });
                confirmation.show();
            }
        });


        for(Button n: no){
            n.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogCofirmationUserInteraction confirmation= new DialogCofirmationUserInteraction(UserInteraction.this, R.string.moechten_sie_das_problem_besteht_nicht_mehr, R.string.problem_ist_solved);
                    confirmation.setPositiveButton(R.string.problem_ist_solved, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            firebaseUtilities.setDataSendListener(new DataSendListener() {
                                @Override
                                public void success() {
                                    Toast.makeText(UserInteraction.this, R.string.thank_you, Toast.LENGTH_SHORT).show();
                                    updateInteractionState();
                                }

                                @Override
                                public void failed(Exception reason) {
                                    Toast.makeText(UserInteraction.this, "Fehler", Toast.LENGTH_SHORT).show();
                                }
                            });

                            String category = "";
                            int id = 0;
                            if (n.equals(no.get(0))) {
                                category = Utilities.CATEGORY_CAFETERIA;
                                id = 3;
                            } else if (n.equals(no.get(1))) {
                                category = Utilities.CATEGORY_COFFEE;
                            } else if (n.equals(no.get(2))) {
                                category = Utilities.CATEGORY_PRINTER;
                            }
                            firebaseUtilities.addToDatabase(category, id);
                        }
                    });
                    confirmation.show();
                }
            });
        }

    }

    private void stateManagement(){

        stateCanteenTV = findViewById(R.id.state_canteen);
        stateCoffeeTV = findViewById(R.id.state_coffee);
        statePrinterTV = findViewById(R.id.state_printer);

        stateCanteen =InteractionState.QUEUE_ABCENT;
        stateCoffee =InteractionState.NORMAL;
        statePrinter =InteractionState.NORMAL;

        stateCanteenTV.setText(getResources().getString(R.string.state)+" "+ stateCanteen.getText());
        stateCoffeeTV.setText(getResources().getString(R.string.state)+" "+ stateCoffee.getText());
        statePrinterTV.setText(getResources().getString(R.string.state)+" "+ statePrinter.getText());

        Resources res = getResources();

        imageBox_canteen = findViewById(R.id.imageBox_canteen);
        imageBox_coffee = findViewById(R.id.imageBox_coffee);
        imageBox_printer = findViewById(R.id.imageBox_printer);

        imageBox_canteen.setBackgroundColor(res.getColor(stateCanteen.getColor()));
        imageBox_coffee.setBackgroundColor(res.getColor(stateCoffee.getColor()));
        imageBox_printer.setBackgroundColor(res.getColor(statePrinter.getColor()));
    }

    private void notificationManagement(){
        notificationCanteenTV = findViewById(R.id.previous_notifications_canteen);
        notificationCoffeeTV = findViewById(R.id.previous_notifications_coffee);
        notificationPrinterTV = findViewById(R.id.previous_notifications_printer);

        notificationCanteen =0;
        notificationCoffee =0;
        notificationPrinter =0;

        notificationCanteenTV.setText(getResources().getString(R.string.previous_notifications)+" "+ notificationCanteen);
        notificationCoffeeTV.setText(getResources().getString(R.string.previous_notifications)+" "+ notificationCoffee);
        notificationPrinterTV.setText(getResources().getString(R.string.previous_notifications)+" "+ notificationPrinter);

    }

    private void updateInteractionState() {
        String[] categories = new String[]{Utilities.CATEGORY_CAFETERIA, Utilities.CATEGORY_COFFEE, Utilities.CATEGORY_PRINTER};
        for (int i=0; i<3; i++) {
            firebaseUtilities.getCurrentStatus(categories[i]);
            firebaseUtilities.getReportCount(categories[i]);
        }
    }

    public InteractionState getStateCanteen() {
        return stateCanteen;
    }

    public void setStateCanteen(InteractionState stateCanteen) {
        this.stateCanteen = stateCanteen;
    }

    public InteractionState getStateCoffee() {
        return stateCoffee;
    }

    public void setStateCoffee(InteractionState stateCoffee) {
        this.stateCoffee = stateCoffee;
    }

    public static InteractionState getStatePrinter() {
        return statePrinter;
    }

    public void setStatePrinter(InteractionState statePrinter) {
        this.statePrinter = statePrinter;
    }

    public long getNotificationCanteen() {
        return notificationCanteen;
    }

    public void setNotificationCanteen(long notificationCanteen) {
        this.notificationCanteen = notificationCanteen;
    }

    public long getNotificationCoffee() {
        return notificationCoffee;
    }

    public void setNotificationCoffee(long notificationCoffee) {
        this.notificationCoffee = notificationCoffee;
    }

    public long getNotificationPrinter() {
        return notificationPrinter;
    }

    public void setNotificationPrinter(long notificationPrinter) {
        this.notificationPrinter = notificationPrinter;
    }


}