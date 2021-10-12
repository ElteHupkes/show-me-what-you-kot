package me.elte.kotit.eps1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import me.elte.kotit.eps1.databinding.FragmentRandomBinding
import kotlin.random.Random

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class RandomFragment : Fragment() {

    private var _binding: FragmentRandomBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    /**
     * The number we'll be communicating back
     */
    private var _myRandomNumber = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Try to fetch our random number from the saved instance state
        _myRandomNumber = savedInstanceState?.getInt(NUMBER_KEY, -1) ?: -1;
        if (_myRandomNumber < 0) {
            generateNumber()
        }

        _binding = FragmentRandomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the number restored / generated in OnCreateView
        updateNumberText()

        binding.buttonRegenerate.setOnClickListener {
            generateNumber()
            updateNumberText()
        }
        binding.buttonSend.setOnClickListener {
            // Create a result, set it, and pop the backstack to return home
            val fm = requireActivity().supportFragmentManager
            val resultBundle = bundleOf(Pair(NUMBER_KEY, _myRandomNumber))
            fm.setFragmentResult(RANDOM_FRAG_REQUEST_KEY, resultBundle)
            fm.popBackStack()
        }
    }

    /**
     * Generates a new random number and updates the text view
     */
    fun generateNumber() {
        _myRandomNumber = Random.nextInt(0, 1000)
    }

    fun updateNumberText() {
        binding.randomNumber.text = resources.getString(R.string.your_number, _myRandomNumber)
    }

    // Called when the fragment is about to be destroyed but it wasn't left explicitly,
    // this is the place where you can store state to be restored later.
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(NUMBER_KEY, _myRandomNumber)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}