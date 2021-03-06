/**
 * Copyright 2015 Moritz Tomasi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.example.moritztomasi.clicklesstextenricherapplication.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.moritztomasi.clicklesstextenricherapplication.common.LanguageSupport;
import com.example.moritztomasi.clicklesstextenricherapplication.dialogues.ChooseToLanguageDialog;
import com.example.moritztomasi.clicklesstextenricherapplication.common.SlidingTabLayout;
import com.example.moritztomasi.clicklesstextenricherapplication.enrichment.Translate;
import com.example.moritztomasi.clicklesstextenricherapplication.common.SupportException;
import com.example.moritztomasi.clicklesstextenricherapplication.common.ValidationException;
import com.example.moritztomasi.clicklesstextenricherapplication.R;
import com.example.moritztomasi.clicklesstextenricherapplication.enrichment.TranslateResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * This is the activity shown when the user want to correct the mistakes made by the ocr engine
 * manually. There is also the option of changing the target language. The associated layout file is
 * activity_retry. If the user chooses to retry the information is again sent to the web service.
 * After that the application returns to {@link ResultActivity}.
 */
public class RetryActivity extends Activity implements
        ChooseToLanguageDialog.ChooseToLanguageListener,
        TranslateResponse {

    private static final String CLASS_TAG = "RetryActivity";
    private static final CharSequence TAB_TITLES[] = {"CORRECT ORIGINAL", "IMAGE"};
    private static final int NUM_TABS = 2;

    private EditText editText;
    private Button toButton;

    private String source;
    private String target;
    private String text;
    private String imagePath;
    private File imageFile;

    /***************************** INIT *****************************/

    /**
     * First method called in Activity lifecycle. Initializes tabs in {@link SlidingTabLayout} and
     * sets color for tab indicator. The information passed on by the intent is stored in local
     * variables.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(CLASS_TAG, "onCreate in RetryActivity called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retry);

        ViewPagerAdapter adapter =  new ViewPagerAdapter();

        ViewPager pager = (ViewPager) findViewById(R.id.pager_car);
        pager.setAdapter(adapter);

        SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.tabs_car);
        tabs.setDistributeEvenly(false);
        tabs.setViewPager(pager);

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.rgb(250, 250, 250);
            }
        });

        Intent intent = getIntent();

        this.source = intent.getExtras().getString("SOURCE_LANGUAGE");
        this.target = intent.getExtras().getString("TARGET_LANGUAGE");
        this.text = intent.getExtras().getString("TEXT");
        this.imagePath = intent.getExtras().getString("IMAGE_PATH");
        this.imageFile = (File) intent.getExtras().get("IMAGE_FILE");
    }

    /***************************** /INIT *****************************/


    /***************************** TRANSLATION SETTINGS *****************************/

    /**
     * Opens a dialog for choosing a target language.
     */
    public void showTranslateToDialog(View view) {
        Log.i(CLASS_TAG, "showTranslateToDialog in RetryActivity called");

        DialogFragment dialog = new ChooseToLanguageDialog();
        dialog.show(getFragmentManager(), "ChooseToLanguageDialog");

        Log.d(CLASS_TAG, "TO dialog is showing");
    }

    /**
     * This method is called by {@link ChooseToLanguageDialog} over the interface
     * {@link ChooseToLanguageDialog.ChooseToLanguageListener} when an option in the dialog is
     * chosen or the dialog is closed. If the dialog is just closed none of the cases are executed.
     * A string representation of the target language is stored in target.
     *
     * @param which Integer indicating which target language was chosen
     */
    @Override
    public void onToLanguageDialogClick(int which) {
        Log.i(CLASS_TAG, "dialog finished, onToLanguageDialogClick in RetryActivity called");

        switch(which) {
            case 0: this.target = "en";
                this.toButton.setText("TO\n" + "English");
                break;
            case 1: this.target = "de";
                this.toButton.setText("TO\n" + "German");
                break;
            case 2: this.target = "it";
                this.toButton.setText("TO\n" + "Italian");
                break;
        }

        Log.d(CLASS_TAG, "target language set to: target=" + this.target);
    }

    /**
     * Show the original image.
     */
    public void showOriginalImage(View view) {
        Log.i(CLASS_TAG, "showOriginalImage in RetryActivity called");

        if(this.imagePath != null && this.imagePath.length() > 0) {
            Intent showImageIntent = new Intent(Intent.ACTION_VIEW);
            showImageIntent.setDataAndType(Uri.parse(this.imagePath), "image/*");
            startActivity(showImageIntent);
        }
        else {
            Log.d(CLASS_TAG, "imagePath cannot be null when showing original image");
            showToast("You have to choose an image.");
        }
    }

    /***************************** /TRANSLATION SETTINGS *****************************/


    /***************************** ENRICHMENT AND START RESULT ACTIVITY *****************************/

    /**
     * Instantiates a {@link Translate} service object and passes source language, target language,
     * the path to the selected image and the corrected text. The next step will be delegated by use of the
     * interface {@link TranslateResponse} and the method {@link TranslateResponse#translateFinished(JSONObject)}.
     * During the execution of this method no progress bar should be showing. If an error occurs a
     * toast will be shown.
     */
    public void go(View view) {
        Log.i(CLASS_TAG, "go in RetryActivity called");

        try {
            Log.i(CLASS_TAG, "instantiation of Translate");

            Translate translate = new Translate();
            translate.translateFromImage(this,
                    this.source,
                    this.target,
                    this.imagePath,
                    this.editText.getText().toString());
        }
        catch(ValidationException e) {
            Log.d(CLASS_TAG, e.getMessage());
            showToast(e.getMessage());
        }
        catch(SupportException e) {
            Log.d(CLASS_TAG, e.getMessage());
            showToast(e.getMessage());
        }
    }

    /**
     * This method is called by {@link Translate} over the interface
     * {@link TranslateResponse} as soon as the web service returns its json response.
     * If the json is null or error is found inside said json, the method returns and shows
     * a toast with the provided error message. Otherwise an {@link Intent} is created and a new
     * activity started. During the execution of this method no progress bar should be showing.
     *
     * @param json Response from web service in form of a json.
     */
    @Override
    public void translateFinished(JSONObject json) {
        Log.i(CLASS_TAG, "translateFinished in MainActivity called");

        if(json == null) {
            Log.d(CLASS_TAG, "json cannot be null");
            showToast("Response faulty");
            return;
        }

        String detected = "";
        String text = "";
        String translation = "";

        try {
            if(json.has("error")) {
                Log.d(CLASS_TAG, json.getString("error"));
                showToast(json.getString("error"));
                return;
            }

            if(json.has("detected")) detected = json.getString("detected");
            if(json.has("text")) text = json.getString("text");
            if(json.has("translation")) translation = json.getString("translation");
        } catch (JSONException e) {
            Log.d(CLASS_TAG, "Exception while checking JSONObject");
            showToast("Response faulty");
            return;
        }

        Intent intent = new Intent(this, ResultActivity.class);

        intent.putExtra("SOURCE_LANGUAGE", this.source);
        intent.putExtra("TARGET_LANGUAGE", this.target);
        intent.putExtra("DETECTED_LANGUAGE", detected);
        intent.putExtra("TEXT", text);
        intent.putExtra("TRANSLATION", translation);
        intent.putExtra("IMAGE_PATH", this.imagePath);
        intent.putExtra("IMAGE_FILE", this.imageFile);

        startActivity(intent);
    }

    /***************************** /ENRICHMENT AND START RESULT ACTIVITY *****************************/


    /***************************** HELPER METHODS *****************************/

    private void showToast(String text) {
        Log.i(CLASS_TAG, "showToast in RetryActivity called");

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);

        toast.show();
    }

    /***************************** /HELPER METHODS *****************************/


    /***************************** HELPER CLASSES *****************************/

    /**
     * Custom ViewPagerAdapter extended from {@link PagerAdapter} for displaying tabs.
     * Contains a tab for the correction of the text, which is represented by the layout
     * tab_correct_text, and a tab for showing the original image, which is represented
     * by the layout tab_original_image. All layout elements inside the tabs, which are
     * changed programmatically, have to be initialized while the tabs themselves are
     * initialized.
     */
    private class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return NUM_TABS;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TAB_TITLES[position];
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = null;
            if(position == 0) {
                view = getLayoutInflater().inflate(R.layout.tab_correct_text, container, false);
                editText = (EditText) view.findViewById(R.id.edit_original_editText);
                editText.setText(text);

                Button fromButton = (Button) view.findViewById(R.id.from_language_button);
                fromButton.setText("FROM\n" + LanguageSupport.convert(source.trim()));

                toButton = (Button) view.findViewById(R.id.to_language_button);
                toButton.setText("TO\n" + LanguageSupport.convert(target.trim()));
            }
            else if(position == 1) {
                view = getLayoutInflater().inflate(R.layout.tab_original_image, container, false);
                ImageView chosenImage = (ImageView) view.findViewById(R.id.original_image_imageView);

                imageFile = new File(imagePath.substring(5, imagePath.length()));
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                chosenImage.setImageBitmap(bitmap);
            }

            if(view != null) container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    /***************************** /HELPER CLASSES *****************************/
}
