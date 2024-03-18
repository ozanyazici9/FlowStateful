package com.ozanyazici.flowstateful

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class FirstScreenVM: ViewModel() {

    private var count = 0

    val counter = flow<Int> {
        while (true) {
            delay(1000L)
            println("running flow")
            emit(count++)
        }
        // Tarih: 18.03.2024
        // lazily diyerek değişkene erişildiğinde intial edilsin diyoruz yani collect edilmeye başlandığında.
        // Fakat diğer ekrana geçtiğimde veya uygulamayı arka plana aldığımda bu sayma işlemi devam ediyor.
        // Fakat uı işlemlerinde uı gözükmezken recomposition edilmesini genellikle istemeyiz.
        // WhileSubscribed bir subscribe olduğu sürece devam et demek. Bu tek başına kullanıldığında
        // uygulamada başka bir ekrana geçildiğinde arkaplanda sürekli recomposition yapılmasını engeller.
        // Bununda bir süresi (zorunlu değil) var diyelimki 5 sn bekle sonra recomposition işlemini durdur diyoruz.
        // Ama uygulama tamamen arka plana alındığında recomposition olmaya devam eder çünkü WhileSubscribed tek başına iken
        // lifecycle farkındalıği yoktur ve arkaplana alındığında onResume un falan çalıştığını anlamaz ve ona göre hareket edemez.
        // Lifecycle a göre de hareket etsin istiyorsak MainActivity de counter.collectAsState demek yerine
        // collectAsStateWithLifecycle demeliyiz. İşte eğer düz bir flow kullanılacaksa böyle Stateful hale getirilir.
        // Normal StatefulFlow da buna gerek yok.
        // Eagerly direkt başlatıyor gözlemlenmeye ihtiyaç duymadan.

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),0)
}