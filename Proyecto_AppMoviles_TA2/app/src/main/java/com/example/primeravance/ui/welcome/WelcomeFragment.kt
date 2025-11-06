package com.example.primeravance.ui.welcome

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.primeravance.R
import com.example.primeravance.databinding.FragmentLoginBinding
import com.example.primeravance.databinding.FragmentWelcomeBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView

class WelcomeFragment : Fragment() {

    private var exoPlayer: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var btnPlay: Button
    private lateinit var btnPause: Button
    private lateinit var btnStop: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_welcome, container, false)

        playerView = view.findViewById(R.id.playerViewWelcome)
        btnPlay = view.findViewById(R.id.btnPlay)
        btnPause = view.findViewById(R.id.btnPause)
        btnStop = view.findViewById(R.id.btnStop)

        val btnInicio = view.findViewById<Button>(R.id.btnInicio)
        btnInicio.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }

        // Inicializar ExoPlayer
        exoPlayer = ExoPlayer.Builder(requireContext()).build()
        playerView.player = exoPlayer

        val uri = Uri.parse("android.resource://${requireContext().packageName}/${R.raw.guia_presentacion}")
        val mediaItem = MediaItem.fromUri(uri)
        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()

        setupControls(uri)

        return view
    }

    private fun setupControls(uri: Uri) {
        btnPlay.setOnClickListener {
            exoPlayer?.playWhenReady = true
        }

        btnPause.setOnClickListener {
            exoPlayer?.pause()
        }

        btnStop.setOnClickListener {
            exoPlayer?.stop()
            val mediaItem = MediaItem.fromUri(uri)
            exoPlayer?.setMediaItem(mediaItem)
            exoPlayer?.prepare()
        }
    }

    override fun onStop() {
        super.onStop()
        exoPlayer?.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        exoPlayer?.release()
        exoPlayer = null
    }
}
