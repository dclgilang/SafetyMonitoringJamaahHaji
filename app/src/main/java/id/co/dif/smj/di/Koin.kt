package id.co.dif.smj.di

import id.co.dif.smj.presentation.fragment.BasicInfoFragment
import id.co.dif.smj.presentation.fragment.ChangeLogFragment
import id.co.dif.smj.presentation.fragment.DashboardFragment
import id.co.dif.smj.presentation.fragment.DetailFragment
import id.co.dif.smj.presentation.fragment.EducationFragment
import id.co.dif.smj.presentation.fragment.HomeFragment
import id.co.dif.smj.presentation.fragment.InboxFragment
import id.co.dif.smj.presentation.fragment.MaintenanceFragment
import id.co.dif.smj.presentation.fragment.MapsTicketFragment
import id.co.dif.smj.presentation.fragment.MeFragment
import id.co.dif.smj.presentation.fragment.MessageNotificationFragment
import id.co.dif.smj.presentation.fragment.MyDashboardFragment
import id.co.dif.smj.presentation.fragment.NotificationFragment
import id.co.dif.smj.presentation.fragment.OverviewFragment
import id.co.dif.smj.presentation.fragment.SiteFragment
import id.co.dif.smj.presentation.fragment.SkillFragment
import id.co.dif.smj.presentation.fragment.TTDashboardFragment
import id.co.dif.smj.presentation.fragment.TimelineFragment
import id.co.dif.smj.presentation.fragment.TroubleTicketFragment
import id.co.dif.smj.presentation.fragment.WorkFragment
import id.co.dif.smj.persistence.DefaultPersistenceOperator
import id.co.dif.smj.persistence.PersistenceOperator
import id.co.dif.smj.persistence.Preferences
import id.co.dif.smj.presentation.fragment.SparePartFragment
import id.co.dif.smj.presentation.fragment.SparePartListFragment
import id.co.dif.smj.utils.AndroidDownloader
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

/***
 * Created by kikiprayudi
 * on Monday, 27/02/23 03:46
 *
 */
val koinModules = module {
    single { provideHttpLogingInterceptor() }

    single { provideChuckerInterceptor(androidContext()) }

    single<OkHttpClient>(named("api")) { provideOkHttpClient(get(), get()) }

    single { provideGsonBuilder() }
    single { AndroidDownloader(androidContext()) }


    single { provideRetrofit(get(named("api")), get()) }
    single { provideLocationClient(androidContext()) }

    factory { provideTripleEApi(get()) }
    single<PersistenceOperator> { DefaultPersistenceOperator() }
    single { Preferences(get()) }

    single { MyDashboardFragment() }
    single { TTDashboardFragment() }
    single { MaintenanceFragment() }
    single { provideColorGenerator() }
    single { DashboardFragment() }
    single { MeFragment() }
    single { NotificationFragment() }
    single { MessageNotificationFragment() }
    single { InboxFragment() }
    single { TroubleTicketFragment() }
    single { HomeFragment() }

    single { DetailFragment() }
    single { SiteFragment() }
    single { TimelineFragment() }
    single { ChangeLogFragment() }
    single { MapsTicketFragment() }
    single { SparePartListFragment() }
    single { SparePartFragment() }


    single { OverviewFragment() }
    single { WorkFragment() }
    single { EducationFragment() }
    single { BasicInfoFragment() }
    single { SkillFragment() }
}