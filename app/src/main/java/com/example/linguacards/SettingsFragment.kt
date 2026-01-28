package com.example.linguacards

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import java.util.Locale
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val spinner = view.findViewById<Spinner>(R.id.spinnerLanguage)

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.languages,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Получаем сохранённый язык
        val prefs = requireContext().getSharedPreferences("settings", 0)
        val savedLang = prefs.getString("language", "ru")
        spinner.setSelection(if (savedLang == "ru") 1 else 0, false)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val lang = when (position) {
                    0 -> "en"
                    1 -> "ru"
                    else -> return
                }

                val prefs = requireContext().getSharedPreferences("settings", 0)
                val savedLang = prefs.getString("language", "ru")

                // чтобы не пересоздавать экран без надобности
                if (lang == savedLang) return

                prefs.edit().putString("language", lang).apply()

                LocaleHelper.setLocale(requireContext(), lang)

                val appLocale = LocaleListCompat.forLanguageTags(lang)
                AppCompatDelegate.setApplicationLocales(appLocale)

//                requireActivity().recreate()

//                AlertDialog.Builder(requireContext())
//                    .setTitle(getString(R.string.language_changed))
//                    .setMessage(getString(R.string.restart_required))
//                    .setCancelable(false)
//                    .setPositiveButton(getString(R.string.restart_now)) { _, _ ->
//                        // Полный перезапуск приложения
//                        val intent = requireActivity().packageManager
//                            .getLaunchIntentForPackage(requireActivity().packageName)
//                        intent?.let {
//                            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//                            startActivity(it)
//                            requireActivity().finish()
//                        }
//
//                    }
//                    .setNegativeButton(getString(R.string.later), null)
//                    .show()

            }

            override fun onNothingSelected(parent: AdapterView<*>) {}

        }

        Log.d("LANG", Locale.getDefault().language)
    }
}
