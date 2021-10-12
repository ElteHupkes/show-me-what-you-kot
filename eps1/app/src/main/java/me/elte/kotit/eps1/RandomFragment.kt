package me.elte.kotit.eps1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import me.elte.kotit.eps1.databinding.HomeFragmentBinding
import me.elte.kotit.eps1.databinding.RandomFragmentBinding
import kotlin.random.Random

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class RandomFragment : Fragment() {

    private var _binding: RandomFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    /**
     * The number we'll be communicating back
     */
    private var _myRandomNumber : Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RandomFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        generateNumber()
        binding.buttonRegenerate.setOnClickListener { generateNumber() }
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
        binding.randomNumber.text = resources.getString(R.string.your_number, _myRandomNumber);
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}