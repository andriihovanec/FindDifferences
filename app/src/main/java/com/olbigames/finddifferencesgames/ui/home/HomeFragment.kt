package com.olbigames.finddifferencesgames.ui.home

import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.olbigames.finddifferencesgames.R
import com.olbigames.finddifferencesgames.db.GameEntity
import com.olbigames.finddifferencesgames.extension.checkCurrentConnection
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(), GamesAdapter.OnItemClickListener {

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: GamesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        val fileDirectory =
            context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath

        viewModel.getGamesSet(fileDirectory)

        viewModel.gamesSet.observe(this, Observer { games ->
            if (games.isEmpty()) {
                if (checkCurrentConnection()) {
                    viewModel.getGamesSet(fileDirectory)
                } else {
                    Toast.makeText(
                        context,
                        "Check you internet connection",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                setupLevelList(games)
            }
        })

        /*if (viewModel.getAnyGames() == null) {
            if (checkCurrentConnection()) {
                viewModel.getGamesSet(fileDirectory)
            } else {
                Toast.makeText(
                    context,
                    "Check you internet connection",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            viewModel.gamesSet.observe(this, Observer { games ->
                val s = games.isEmpty()
                val b = s
                setupLevelList(games)
            })
        }*/
    }

    private fun setupLevelList(list: List<GameEntity>) {
        Toast.makeText(activity, "${list.size}", Toast.LENGTH_SHORT).show()
        adapter = GamesAdapter(this)
        games_recyclerview.adapter = adapter
        games_recyclerview.layoutManager = GridLayoutManager(context, 2)
        games_recyclerview.setHasFixedSize(true)
        adapter.setupGames(list)
    }

    override fun onItemClicked(game: GameEntity) {
        val bundle = Bundle()
        bundle.putString("level", game.uri)
        findNavController().navigate(R.id.gameFragment, bundle)
    }
}
