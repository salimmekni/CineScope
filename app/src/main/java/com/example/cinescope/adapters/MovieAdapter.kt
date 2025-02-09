import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cinescope.databinding.ItemMovieBinding
import com.example.cinescope.api.Movie

class MovieAdapter(private val movies: List<Movie>, private val onMovieClick: (Movie) -> Unit) :
    RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.movieTitle.text = movie.title
            binding.movieYear.text = "Année : ${movie.year}" // Ajout de l'année
            Glide.with(binding.root.context)
                .load(movie.poster)
                .placeholder(android.R.drawable.ic_menu_report_image) // Placeholder si image absente
                .into(binding.moviePoster)

            binding.root.setOnClickListener { onMovieClick(movie) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount() = movies.size
}
