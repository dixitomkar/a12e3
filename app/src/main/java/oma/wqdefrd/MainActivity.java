package oma.wqdefrd;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class MainActivity extends Activity implements View.OnTouchListener        // onouchListener will be overriden for gestures detection.
{
    private ViewGroup Main_Layout;                                                // Variable for main layout.
    private int x_position;                                                       // Variable to track x co-ordinate of touchpad
    private int y_position;                                                       // Variable to track y co-ordinate of touchpad
    long touch_start=0;                                                           // Initialize to 0 to check double tap.
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Main_Layout = (ViewGroup) findViewById(R.id.input_layout);                // Defined Main layout with button add new image in activity_main.xml

        Button image_add_new = (Button)findViewById(R.id.image_new);              // Added new button image_view which is defined in activity_main.xml
        image_add_new.setOnClickListener(new View.OnClickListener()               // setonClickListener executes function when we click the button
        {
            @Override
            public void onClick(View v)                                           // Override Onclick (when user clicks the button)
            {
                Add_Image();                                                      // Function to add new image. It is defined below.
            }
        });
    }


    private void Add_Image()                                                       // Function to add new image
    {
        final ImageView image_view = new ImageView(this);
        image_view.setImageResource(R.drawable.a1);                                // Reference to image defined in drawables.
        RelativeLayout.LayoutParams add_layout = new RelativeLayout.LayoutParams(250, 250);   // Size of image
        image_view.setLayoutParams(add_layout);
        Main_Layout.addView(image_view, add_layout);                                // Add Image view to current layout.
        image_view.setOnTouchListener(this);                                        // Defined touch display to image
    }


    public boolean onTouch(final View new_view, MotionEvent new_event)              // Main function to detect gestures.
    {
        final int touch_x = (int) new_event.getRawX();                                    // x co-rdinate of touch event
        final int touch_y = (int) new_event.getRawY();                                    // y co-oidinate of touch event
        int pointerCount = new_event.getPointerCount();                             // Get number of touch. Based on this, we have defined different gestures and different actions

        switch (new_event.getAction() & MotionEvent.ACTION_MASK)                    // This is used to handle multi touch
        {

            case MotionEvent.ACTION_DOWN:                                           // ACTION_DOWN is indicator of user touches the screen
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) new_view.getLayoutParams();
                x_position = touch_x - layoutParams.leftMargin;                           // new x position = position of touch - leftmargin.
                y_position = touch_y - layoutParams.topMargin;                            // new y position = position of touch - top.
                break;

            case MotionEvent.ACTION_UP:                                                  // This is triggers when user leaves the screen
                if (touch_start == 0)                                                   // If this is first touch then collect timestamp
                {
                    touch_start = System.currentTimeMillis();
                }
                else                                                                    // If this is second tap
                {
                    if (System.currentTimeMillis() - touch_start < 300)                 // check if it is double tap.
                    {
                        AlertDialog.Builder temp1 = new AlertDialog.Builder(MainActivity.this);
                        temp1.setMessage("Confirm deletion");                           // display alert box confirm deletion
                        temp1.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)     // If response is yes then remove visibility of the object.
                            {
                                new_view.setVisibility(View.GONE);
                            }
                        });

                        temp1.setNegativeButton("No", new DialogInterface.OnClickListener() // If response is no then remove dialog box.
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alertDialog = temp1.create();
                        alertDialog.show();

                    }

                    touch_start = System.currentTimeMillis();                                   // for next double tap, init touch_start variable

                }
                break;

            case MotionEvent.ACTION_MOVE:                                                 // To track actions
                if (pointerCount == 1)                                                    // Single pointer gesture is used to move the image.
                {                                                                         // Here Logic for image movement is that, when we select image and drag fingure/pointer ,
                    RelativeLayout.LayoutParams new_parameters = (RelativeLayout.LayoutParams) new_view.getLayoutParams();
                    new_parameters.leftMargin = touch_x - x_position;                     // left margin = position of touch event - X value of touch when touching started
                    new_parameters.topMargin = touch_y - y_position;                      // upper margin = position of touch event - Y value of touch when touching started
                    new_parameters.rightMargin = -400;                                    // right margin is set to -400 so that if image goes after right margin, partially image will be shown
                    new_parameters.bottomMargin = -400;                                   // bottom margin is set to -400 so that if image goes after right margin, partially image will be shown
                    new_view.setLayoutParams(new_parameters);                             // New view is displayed.
                }

                if (pointerCount == 2)                                                    // This is for 2 fingers gesture.
                {
                    RelativeLayout.LayoutParams layoutParams1 =  (RelativeLayout.LayoutParams) new_view.getLayoutParams();
                    layoutParams1.width = x_position +(int)new_event.getX();              // set width according to user position
                    layoutParams1.height = y_position + (int)new_event.getY();           // set height according to user leaving position.
                    new_view.setLayoutParams(layoutParams1);
                }

                //Rotation
                if (pointerCount == 3)                                                  // 3 fingures gesture is defined as rotation.
                {
                    //Rotate the ImageView
                    new_view.setRotation(new_view.getRotation() + 30.0f);               // for rotaion.
                }

                break;
        }

        Main_Layout.invalidate();                                                         // Thus deletes previous view as we have to display modified view
        return true;
    }
}
