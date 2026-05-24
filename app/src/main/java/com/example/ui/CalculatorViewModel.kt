package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.HistoryEntity
import com.example.data.HistoryRepository
import com.example.utils.Evaluator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: HistoryRepository

    val history: StateFlow<List<HistoryEntity>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = HistoryRepository(database.historyDao())
        history = repository.allHistory.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    private val _expression = MutableStateFlow("")
    val expression = _expression.asStateFlow()

    private val _result = MutableStateFlow("")
    val result = _result.asStateFlow()

    private val _isAdvancedMode = MutableStateFlow(false)
    val isAdvancedMode = _isAdvancedMode.asStateFlow()

    private val _isHistoryOpen = MutableStateFlow(false)
    val isHistoryOpen = _isHistoryOpen.asStateFlow()

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number -> append(action.number.toString())
            is CalculatorAction.Operator -> append(action.operator)
            is CalculatorAction.Clear -> {
                _expression.value = ""
                _result.value = ""
            }
            is CalculatorAction.Delete -> {
                if (_expression.value.isNotEmpty()) {
                    _expression.value = _expression.value.dropLast(1)
                    calculatePreview()
                }
            }
            is CalculatorAction.Calculate -> {
                if (_expression.value.isNotEmpty()) {
                    try {
                        val evalResult = Evaluator.evaluate(_expression.value)
                        val formattedResult = formatResult(evalResult)
                        
                        viewModelScope.launch {
                            repository.insert(HistoryEntity(
                                expression = _expression.value,
                                result = formattedResult
                            ))
                        }
                        
                        _expression.value = formattedResult
                        _result.value = ""
                    } catch (e: Exception) {
                        _result.value = "Error"
                    }
                }
            }
            is CalculatorAction.ToggleAdvanced -> _isAdvancedMode.value = !_isAdvancedMode.value
            is CalculatorAction.ToggleHistory -> _isHistoryOpen.value = !_isHistoryOpen.value
            is CalculatorAction.Function -> append("${action.func}(")
            is CalculatorAction.Constant -> append(action.constant)
            is CalculatorAction.Parenthesis -> append(action.paren)
            is CalculatorAction.Decimal -> append(".")
            is CalculatorAction.ClearHistory -> viewModelScope.launch { repository.clearHistory() }
            is CalculatorAction.LoadHistory -> {
                _expression.value = action.history.expression
                _isHistoryOpen.value = false
            }
        }
    }

    private fun append(str: String) {
        _expression.value += str
        calculatePreview()
    }

    private fun calculatePreview() {
        if (_expression.value.isEmpty()) {
            _result.value = ""
            return
        }
        try {
            val evalResult = Evaluator.evaluate(_expression.value)
            _result.value = formatResult(evalResult)
        } catch (e: Exception) {
            _result.value = "" // Silently fail for preview
        }
    }

    private fun formatResult(result: Double): String {
        return if (result == result.toLong().toDouble()) {
            result.toLong().toString()
        } else {
            val df = DecimalFormat("#.########")
            df.format(result).replace(",", ".")
        }
    }
}

sealed class CalculatorAction {
    data class Number(val number: Int) : CalculatorAction()
    data class Operator(val operator: String) : CalculatorAction()
    data class Function(val func: String) : CalculatorAction()
    data class Constant(val constant: String) : CalculatorAction()
    data class Parenthesis(val paren: String) : CalculatorAction()
    object Clear : CalculatorAction()
    object Delete : CalculatorAction()
    object Calculate : CalculatorAction()
    object ToggleAdvanced : CalculatorAction()
    object ToggleHistory : CalculatorAction()
    object Decimal : CalculatorAction()
    object ClearHistory : CalculatorAction()
    data class LoadHistory(val history: HistoryEntity) : CalculatorAction()
}
