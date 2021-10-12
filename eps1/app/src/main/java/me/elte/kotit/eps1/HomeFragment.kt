package me.elte.kotit.eps1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import me.elte.kotit.eps1.databinding.FragmentHomeBinding

// Request keys for activity / fragment random number fetching
const val RANDOM_FRAG_REQUEST_KEY = "random_request"
const val NUMBER_KEY = "random_number"

/**
 * Activity result passing contract for the Activity Result API
 */
class IntegerResultContract : ActivityResultContract<Unit, Int>() {
    override fun parseResult(resultCode: Int, intent: Intent?): Int = when(resultCode) {
        Activity.RESULT_OK -> intent?.getIntExtra(NUMBER_KEY, -1) ?: -1
        else -> -1
    }

    override fun createIntent(context: Context, input: Unit): Intent {
        return Intent(context, RandomActivity::class.java)
    }

}

/**
 * The home fragment for our app, displays a generated number received from either
 * the other activity, or the RandomFragment
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // The number returned from our fragment
    private var _selectedNumber = -1

    private val _activityLauncher : ActivityResultLauncher<Unit> = registerForActivityResult(IntegerResultContract()) {
        _selectedNumber = it;
        updateNumberText()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Try to restore the saved number
        _selectedNumber = savedInstanceState?.getInt(NUMBER_KEY, -1) ?: -1;
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

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

    // Like I said, view models and bindings are saved for next time..
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
            ) { _, choice ->
                when (choice) {
                    0 -> launchActivity()
                    else -> launchFragment()
                }
            }
        builder.show()
    }

    fun launchActivity() {
        _activityLauncher.launch(Unit)
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