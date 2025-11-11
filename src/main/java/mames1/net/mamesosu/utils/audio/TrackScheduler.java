package mames1.net.mamesosu.utils.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import mames1.net.mamesosu.constants.LogLevel;
import mames1.net.mamesosu.utils.log.AppLogger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class TrackScheduler extends AudioEventAdapter {

    public final AudioPlayer audioPlayer;
    public final BlockingDeque<AudioTrack> queue;

    public TrackScheduler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.queue = new LinkedBlockingDeque<>();
    }

    public void queue(AudioTrack track) {
        if (!this.audioPlayer.startTrack(track, true)) {
            this.queue.offer(track);
        }
    }

    public void nextTrack() {
        AudioTrack track = this.queue.poll();
        this.audioPlayer.startTrack(track, false);
    }

    // VC切断時に呼び出し。再生停止＋キュー全消去（未再生ファイルも削除）
    public synchronized void stopAndClear() {
        // 再生中のトラックを保持してから停止
        AudioTrack current = this.audioPlayer.getPlayingTrack();
        try {
            this.audioPlayer.stopTrack();
        } catch (Exception ignored) { }

        // 現在再生中だったファイルを削除
        deleteLocalFileSafe(current);

        // キューのスナップショットを取ってからクリアし、全件削除
        AudioTrack[] remaining = this.queue.toArray(new AudioTrack[0]);
        this.queue.clear();
        for (AudioTrack t : remaining) {
            deleteLocalFileSafe(t);
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();
        }
        deleteLocalFileSafe(track);
    }

    // ローカルファイルだけ安全に削除（URL等は対象外）
    private void deleteLocalFileSafe(AudioTrack track) {
        if (track == null) return;
        String id = track.getIdentifier();
        if (id == null) return;

        // URL等のスキーム付き識別子は無視
        if (id.matches("^[a-zA-Z]+://.*")) return;

        try {
            Files.deleteIfExists(Path.of(id));
        } catch (Exception e) {
            AppLogger.log("オーディオファイル削除中にエラー: " + e.getMessage(), LogLevel.ERROR);
        }
    }
}
