package com.misiojab.mj.mjound;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.support.annotation.Nullable;
import android.util.Log;


import java.util.List;

public class NotificationListener extends NotificationListenerService {
    MediaSessionManager msManager;
    ComponentName componentName = new ComponentName("com.misiojab.mj.mjound", "com.misiojab.mj.mjound.NotificationListener");
    MediaController mediaController;
    public static boolean online;

    private MediaMetadata meta;

    public void onCreate(){
        Log.e("NotificationListener: ","onCreate załapał");
        msManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        msManager.addOnActiveSessionsChangedListener(sessionListener, componentName);
        List<MediaController> controllers = msManager.getActiveSessions(componentName);
        mediaController = pickController(controllers);
        if(mediaController!=null) {
            mediaController.registerCallback(callback);
            meta = mediaController.getMetadata();
            updateMetadata();
            Log.e("NotificationListener: ","Metadata i controllery pyknęły");
        }
        online = true;

        // sendBroadcast!!!!
    }

    MediaSessionManager.OnActiveSessionsChangedListener sessionListener = new MediaSessionManager.OnActiveSessionsChangedListener() {
        @Override
        public void onActiveSessionsChanged(@Nullable List<MediaController> controllers) {
            mediaController = pickController(controllers);
            if(mediaController==null) return;
            mediaController.registerCallback(callback);
            meta = mediaController.getMetadata();
            updateMetadata();
            Log.e("NotificationListener: ","MediaSessionManager uruchomił sessionListenera");
            // sendBroadcast(new Intent(StandardWidget.WIDGET_UPDATE));
        }
    };

    private MediaController pickController(List<MediaController> controllers){
        for(int i=0;i<controllers.size();i++){
            MediaController mc = controllers.get(i);
            if(mc!=null&&mc.getPlaybackState()!=null&&
                    mc.getPlaybackState().getState()== PlaybackState.STATE_PLAYING){
                return mc;
            }
        }
        if(controllers.size()>0) return controllers.get(0);
        return null;
    }

    MediaController.Callback callback = new MediaController.Callback() {
        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
            mediaController = null;
            meta = null;
        }

        @Override
        public void onSessionEvent(String event, Bundle extras) {
            super.onSessionEvent(event, extras);
            updateMetadata();
            //sendBroadcast(new Intent(StandardWidget.WIDGET_UPDATE));
        }

        @Override
        public void onPlaybackStateChanged(PlaybackState state) {
            super.onPlaybackStateChanged(state);
            //StandardWidget.currentlyPlaying = state.getState() == PlaybackState.STATE_PLAYING;
            updateMetadata();
            //sendBroadcast(new Intent(StandardWidget.WIDGET_UPDATE));
        }

        @Override
        public void onMetadataChanged(MediaMetadata metadata) {
            super.onMetadataChanged(metadata);
            meta = metadata;
            updateMetadata();

            // sendBroadcast(new Intent(StandardWidget.WIDGET_UPDATE));
        }

        @Override
        public void onQueueTitleChanged(CharSequence title) {
            super.onQueueTitleChanged(title);
            updateMetadata();
        }


        @Override
        public void onAudioInfoChanged(MediaController.PlaybackInfo info) {
            super.onAudioInfoChanged(info);
            updateMetadata();
        }
    };

    @SuppressLint("WrongConstant")
    public void updateMetadata(){
        if(mediaController!=null&&mediaController.getPlaybackState()!=null){
            //StandardWidget.currentlyPlaying = mediaController.getPlaybackState().getState() == PlaybackState.STATE_PLAYING;
        }
        if(meta==null)return;
        /*
        StandardWidget.currentArt=meta.getBitmap(MediaMetadata.METADATA_KEY_ART);
        if(StandardWidget.currentArt==null){
            StandardWidget.currentArt = meta.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART);
        }
        if(StandardWidget.currentArt==null){
            StandardWidget.currentArt = meta.getBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON);
        }
        StandardWidget.currentSquareArt = StandardWidget.currentArt;
        if(StandardWidget.currentArt!=null) {
            int cah = StandardWidget.currentArt.getHeight();
            int caw = StandardWidget.currentArt.getWidth();
            if (caw > cah * 1.02) {
                StandardWidget.currentSquareArt = Bitmap.createBitmap(StandardWidget.currentArt,
                        (int) (caw / 2 - cah * 0.51), 0, (int) (cah * 1.02), cah);
            }
        }
        StandardWidget.currentArtist=meta.getString(MediaMetadata.METADATA_KEY_ARTIST);
        StandardWidget.currentSong=meta.getString(MediaMetadata.METADATA_KEY_TITLE);
        if(StandardWidget.currentSong==null){
            StandardWidget.currentSong=meta.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE);
        }
        StandardWidget.currentAlbum=meta.getString(MediaMetadata.METADATA_KEY_ALBUM);
        if(StandardWidget.currentArtist==null){
            StandardWidget.currentArtist = meta.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST);
        }
        if(StandardWidget.currentArtist==null) {
            StandardWidget.currentArtist = meta.getString(MediaMetadata.METADATA_KEY_AUTHOR);
        }
        if(StandardWidget.currentArtist==null) {
            StandardWidget.currentArtist = meta.getString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE);
        }
        if(StandardWidget.currentArtist==null) {
            StandardWidget.currentArtist = meta.getString(MediaMetadata.METADATA_KEY_WRITER);
        }
        if(StandardWidget.currentArtist==null) {
            StandardWidget.currentArtist = meta.getString(MediaMetadata.METADATA_KEY_COMPOSER);
        }
        if(StandardWidget.currentArtist==null)StandardWidget.currentArtist = "";
        if(StandardWidget.currentSong==null)StandardWidget.currentSong = "";
        if(StandardWidget.currentAlbum==null)StandardWidget.currentAlbum = "";
        sendBroadcast(new Intent(StandardWidget.WIDGET_UPDATE));
        */
        if (meta.getString(MediaMetadata.METADATA_KEY_TITLE) != null)
            Log.e(" .METADATA_KEY_TITLE", meta.getString(MediaMetadata.METADATA_KEY_TITLE));
        if (meta.getString(MediaMetadata.METADATA_KEY_GENRE) != null)
            Log.e(" .METADATA_KEY_GENRE", meta.getString(MediaMetadata.METADATA_KEY_GENRE));
        if (meta.getString(MediaMetadata.METADATA_KEY_ALBUM) != null)
            Log.e(" .METADATA_KEY_ALBUM", meta.getString(MediaMetadata.METADATA_KEY_ALBUM));
        if (meta.getString(MediaMetadata.METADATA_KEY_ARTIST) != null)
            Log.e(" .METADATA_KEY_ARTIST", meta.getString(MediaMetadata.METADATA_KEY_ARTIST));
        if (meta.getString(MediaMetadata.METADATA_KEY_AUTHOR) != null)
            Log.e(" .METADATA_KEY_ARTIST", meta.getString(MediaMetadata.METADATA_KEY_AUTHOR));
        if (meta.getString(MediaMetadata.METADATA_KEY_COMPOSER) != null)
            Log.e(" .METADATA_KEY_ARTIST", meta.getString(MediaMetadata.METADATA_KEY_COMPOSER));
        if (meta.getString(MediaMetadata.METADATA_KEY_DURATION) != null)
            Log.e(" .METADATA_KEY_ARTIST", meta.getString(MediaMetadata.METADATA_KEY_DURATION));
    }


}
