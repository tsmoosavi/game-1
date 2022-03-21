package com.example.game

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.game.databinding.Fragment1Binding


class Fragment1 : Fragment() {
    lateinit var binding: Fragment1Binding
    val vModel:Fragment1ViewModel by viewModels()
    var btnArray=ArrayList<Button>()
    var arrayOfRandoms=ArrayList<Int>()
    var flagDice=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = Fragment1Binding.inflate (inflater, container, false)
        return binding.root
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_1, container, false)
    }

    @SuppressLint("CutPasteId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


            view.findViewById<TextView>(R.id.aNumber_txv).text = vModel.aNumber
            view.findViewById<TextView>(R.id.bNumber_txv).text = vModel.bNumber
            view.findViewById<TextView>(R.id.text_view_comment).text = vModel.comment
            view.findViewById<Button>(R.id.answer1_btn).text = vModel.textOfAnswer1Btn
            view.findViewById<Button>(R.id.answer2_btn).text = vModel.textOfAnswer2Btn
            view.findViewById<Button>(R.id.answer3_btn).text = vModel.textOfAnswer3Btn
            view.findViewById<Button>(R.id.answer4_btn).text = vModel.textOfAnswer4Btn
            view.findViewById<Button>(R.id.answer1_btn).isEnabled = vModel.enable
            view.findViewById<Button>(R.id.answer2_btn).isEnabled = vModel.enable
            view.findViewById<Button>(R.id.answer3_btn).isEnabled = vModel.enable
            view.findViewById<Button>(R.id.answer4_btn).isEnabled = vModel.enable
            view.findViewById<TextView>(R.id.score_txv).text= vModel.textOfScoreTxw



        val pref = requireActivity().getSharedPreferences("share", Context.MODE_PRIVATE)
        if (!(pref.getString("maxScore","")).isNullOrBlank()) {
            Storage.maxScore = pref.getString("maxScore", "").toString().toInt()
        }

        btnArray = arrayListOf(binding.answer1Btn,binding.answer2Btn
            ,binding.answer3Btn,binding.answer4Btn)


        dice()

        binding.diceBtn.setOnClickListener {

            Storage.questionNumber++
            if (Storage.questionNumber>=6){
                if (Storage.maxScore<Storage.score){
                    Storage.maxScore=Storage.score
                }
                Storage.questionNumber=1
                findNavController().navigate(R.id.action_fragment1_to_fragment2)
            }
            else {
                for (button in btnArray) {
                    button.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.purple_501))
                }
                enableButton()
                flagDice=true
                dice()

            }
        }

        saveOnViewModel()
    }






    fun setTextButton(number:Int){
        for (i in btnArray.indices){
            if (number == i){
                btnArray[i].text =Storage.result.toString()
            }else{
                var flag=true
                while(flag){
                    val rand=Storage.getRandom()
                    if(rand !in arrayOfRandoms){
                        btnArray[i].text=rand.toString()
                        arrayOfRandoms.add(rand)
                        flag=false
                    }
                }

            }

        }
    }


    fun correctAnswer(button: Button){
        if(button.text==Storage.result.toString()){
            Storage.score+=5
            binding.scoreTxv.text=Storage.score.toString()
            button.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.green))
            disableButton()
        } else{
            Storage.score-=2
            binding.scoreTxv.text=Storage.score.toString()
            button.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.red))
            disableButton()
        }

    }

    private fun disableButton() {
        for (button in btnArray){
            button.isEnabled=false
        }
    }

    private fun enableButton() {
        for (button in btnArray){
            button.isEnabled=true
        }
    }

    fun dice() {
        if(binding.aNumberTxv.text.isBlank()||flagDice) {
            Storage.result = calculateResult()
            arrayOfRandoms.add(Storage.result)

            binding.aNumberTxv.text = Storage.a.toString()
            binding.bNumberTxv.text = Storage.b.toString()
            binding.scoreTxv.text = Storage.score.toString()
            when(Storage.operator){
                "+"-> binding.textViewComment.text="مجموع دو عدد بالا را حدس بزنید"
                "-"-> binding.textViewComment.text="نتیجه تفریق عدد پایینی از عدد بالایی کدام است؟"
                "*"-> binding.textViewComment.text="حاصل ضرب دو عدد بالا را حدس بزنید"
                "/"-> binding.textViewComment.text="خارج قسمت تقسیم عدد بالایی بر عدد پایینی کدام است؟"
                "%"-> binding.textViewComment.text="باقیمانده تقسیم عدد بالایی بر عدد پایینی کدام است؟"
            }
            val numRandom = (0..3).random()
            setTextButton(numRandom)
        }
        for (button in btnArray){
            button.setOnClickListener {
                correctAnswer(button)
                for (button in btnArray){
                    if(button.text==Storage.result.toString()){
                        button.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.green))
                    }
                }
            }
        }


    }

    private fun calculateResult():Int {
        Storage.a=Storage.randomNumberA()
        Storage.b=Storage.randomNumberB()
       return when (Storage.operator){
            "+" -> Storage.a+Storage.b
            "-" -> Storage.a-Storage.b
            "*" -> Storage.a*Storage.b
            "/" -> Storage.a/Storage.b
            else -> Storage.a%Storage.b
        }
    }


     fun saveOnViewModel() {
        vModel.aNumber=view?.findViewById<TextView>(R.id.aNumber_txv)?.text.toString()
        vModel.bNumber=view?.findViewById<TextView>(R.id.bNumber_txv)?.text.toString()
        vModel.comment=view?.findViewById<TextView>(R.id.text_view_comment)?.text.toString()
        vModel.textOfAnswer1Btn=view?.findViewById<Button>(R.id.answer1_btn)?.text.toString()
        vModel.textOfAnswer2Btn=view?.findViewById<Button>(R.id.answer2_btn)?.text.toString()
        vModel.textOfAnswer3Btn=view?.findViewById<Button>(R.id.answer3_btn)?.text.toString()
        vModel.textOfAnswer4Btn=view?.findViewById<Button>(R.id.answer4_btn)?.text.toString()
        vModel.textOfScoreTxw=view?.findViewById<TextView>(R.id.score_txv)?.text.toString()
        vModel.enable=binding.answer4Btn.isEnabled


    }

}
