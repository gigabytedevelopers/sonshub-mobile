package com.gigabytedevelopersinc.apps.sonshub.players.music.ui.viewmodels

import com.gigabytedevelopersinc.apps.sonshub.players.music.models.MediaID
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelsModule = module {
    viewModel {
        MainViewModel(get(), get(), get(), get(), get(), get())
    }

    viewModel {
        SearchViewModel(get(), get(), get())
    }

    viewModel { (mediaId: MediaID) ->
        MediaItemFragmentViewModel(mediaId, get())
    }

    viewModel {
        NowPlayingViewModel(get())
    }
}
