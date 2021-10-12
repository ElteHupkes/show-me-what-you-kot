package me.elte.kotit.eps1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import me.elte.kotit.eps1.databinding.HomeFragmentBinding

// Request keys for activity / fragment random number fetching
const val RANDOM_REQUEST_KEY = 994
const val RANDOM_FRAG_REQUEST_KEY = "random_request"
const val NUMBER_KEY = "random_number"

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class HomeFragment : Fragment() {

    private var _binding: HomeFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // The number returned from our fragment
    private var _selectedNumber = -1

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Try to restore the saved number
        _selectedNumber = savedInstanceState?.getInt(NUMBER_KEY, -1) ?: -1;
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Show the dialog when we click the get number button
        binding.buttonGetNumber.setOnClickListener { showGetNumberDialog() }
        updateNumberText()

        // Set a listener using the Fragment Result API:
        // https://developer.android.com/guide/fragments/communicate#kotlin
        // It would be much easier to just have a shared view model, but... I said
        // I wasn't using view models / data binding for this sample, so I won't.
        requireActivity().supportFragmentManager
            .setFragmentResultListener(RANDOM_FRAG_REQUEST_KEY, this) { _, bundle ->
                _selectedNumber = bundle.getInt(NUMBER_KEY, -1)
                updateNumberText()
            }
    }

    fun updateNumberText() {
        binding.returnedNumber.text = if (_selectedNumber >= 0)
            resources.getString(R.string.selected_number, _selectedNumber)
        else resources.getString(R.string.no_number_yet)
    }

    /**
     * Shows a dialog that lets the user pick either a
     */
    fun showGetNumberDialog() {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle(R.string.activity_or_fragment)
            .setItems(
                arrayOf(
                    resources.getString(R.string.pick_activity),
                    resources.getString(R.string.pick_fragment)
                )
            ) { dialog, choice ->
                when (choice) {
                    0 -> launchActivity()
                    else -> launchFragment()
                }
            }
        builder.show()
    }

    fun launchActivity() {
        TODO("Not implemented yet")
    }

    fun launchFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, RandomFragment())
            .addToBackStack("random")
            .commit()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (_selectedNumber >= 0) {
            outState.putInt(NUMBER_KEY, _selectedNumber)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}