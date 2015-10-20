package com.carl.netspeednotification.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.carl.netspeednotification.App;
import com.carl.netspeednotification.utils.PreferenceUtils;
import com.carl.netspeednotification.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkManager {

    public final static int SPEED_CACHE_MAX = 20;

    private final static float UNIT_KB = 1000;
    private final static float UNIT_MB = 1000*UNIT_KB;
    private final static float UNIT_GB = 1000*UNIT_MB;
    private final static float UNIT_TB = 1000*UNIT_GB;

    private final static int[] SPEED_NOTIF_ICONS_WKB = {R.drawable.wkb000,R.drawable.wkb001,R.drawable.wkb002,R.drawable.wkb003,R.drawable.wkb004,R.drawable.wkb005,R.drawable.wkb006,R.drawable.wkb007,R.drawable.wkb008,R.drawable.wkb009,R.drawable.wkb010,R.drawable.wkb011,R.drawable.wkb012,R.drawable.wkb013,R.drawable.wkb014,R.drawable.wkb015,R.drawable.wkb016,R.drawable.wkb017,R.drawable.wkb018,R.drawable.wkb019,R.drawable.wkb020,R.drawable.wkb021,R.drawable.wkb022,R.drawable.wkb023,R.drawable.wkb024,R.drawable.wkb025,R.drawable.wkb026,R.drawable.wkb027,R.drawable.wkb028,R.drawable.wkb029,R.drawable.wkb030,R.drawable.wkb031,R.drawable.wkb032,R.drawable.wkb033,R.drawable.wkb034,R.drawable.wkb035,R.drawable.wkb036,R.drawable.wkb037,R.drawable.wkb038,R.drawable.wkb039,R.drawable.wkb040,R.drawable.wkb041,R.drawable.wkb042,R.drawable.wkb043,R.drawable.wkb044,R.drawable.wkb045,R.drawable.wkb046,R.drawable.wkb047,R.drawable.wkb048,R.drawable.wkb049,R.drawable.wkb050,R.drawable.wkb051,R.drawable.wkb052,R.drawable.wkb053,R.drawable.wkb054,R.drawable.wkb055,R.drawable.wkb056,R.drawable.wkb057,R.drawable.wkb058,R.drawable.wkb059,R.drawable.wkb060,R.drawable.wkb061,R.drawable.wkb062,R.drawable.wkb063,R.drawable.wkb064,R.drawable.wkb065,R.drawable.wkb066,R.drawable.wkb067,R.drawable.wkb068,R.drawable.wkb069,R.drawable.wkb070,R.drawable.wkb071,R.drawable.wkb072,R.drawable.wkb073,R.drawable.wkb074,R.drawable.wkb075,R.drawable.wkb076,R.drawable.wkb077,R.drawable.wkb078,R.drawable.wkb079,R.drawable.wkb080,R.drawable.wkb081,R.drawable.wkb082,R.drawable.wkb083,R.drawable.wkb084,R.drawable.wkb085,R.drawable.wkb086,R.drawable.wkb087,R.drawable.wkb088,R.drawable.wkb089,R.drawable.wkb090,R.drawable.wkb091,R.drawable.wkb092,R.drawable.wkb093,R.drawable.wkb094,R.drawable.wkb095,R.drawable.wkb096,R.drawable.wkb097,R.drawable.wkb098,R.drawable.wkb099,R.drawable.wkb100,R.drawable.wkb101,R.drawable.wkb102,R.drawable.wkb103,R.drawable.wkb104,R.drawable.wkb105,R.drawable.wkb106,R.drawable.wkb107,R.drawable.wkb108,R.drawable.wkb109,R.drawable.wkb110,R.drawable.wkb111,R.drawable.wkb112,R.drawable.wkb113,R.drawable.wkb114,R.drawable.wkb115,R.drawable.wkb116,R.drawable.wkb117,R.drawable.wkb118,R.drawable.wkb119,R.drawable.wkb120,R.drawable.wkb121,R.drawable.wkb122,R.drawable.wkb123,R.drawable.wkb124,R.drawable.wkb125,R.drawable.wkb126,R.drawable.wkb127,R.drawable.wkb128,R.drawable.wkb129,R.drawable.wkb130,R.drawable.wkb131,R.drawable.wkb132,R.drawable.wkb133,R.drawable.wkb134,R.drawable.wkb135,R.drawable.wkb136,R.drawable.wkb137,R.drawable.wkb138,R.drawable.wkb139,R.drawable.wkb140,R.drawable.wkb141,R.drawable.wkb142,R.drawable.wkb143,R.drawable.wkb144,R.drawable.wkb145,R.drawable.wkb146,R.drawable.wkb147,R.drawable.wkb148,R.drawable.wkb149,R.drawable.wkb150,R.drawable.wkb151,R.drawable.wkb152,R.drawable.wkb153,R.drawable.wkb154,R.drawable.wkb155,R.drawable.wkb156,R.drawable.wkb157,R.drawable.wkb158,R.drawable.wkb159,R.drawable.wkb160,R.drawable.wkb161,R.drawable.wkb162,R.drawable.wkb163,R.drawable.wkb164,R.drawable.wkb165,R.drawable.wkb166,R.drawable.wkb167,R.drawable.wkb168,R.drawable.wkb169,R.drawable.wkb170,R.drawable.wkb171,R.drawable.wkb172,R.drawable.wkb173,R.drawable.wkb174,R.drawable.wkb175,R.drawable.wkb176,R.drawable.wkb177,R.drawable.wkb178,R.drawable.wkb179,R.drawable.wkb180,R.drawable.wkb181,R.drawable.wkb182,R.drawable.wkb183,R.drawable.wkb184,R.drawable.wkb185,R.drawable.wkb186,R.drawable.wkb187,R.drawable.wkb188,R.drawable.wkb189,R.drawable.wkb190,R.drawable.wkb191,R.drawable.wkb192,R.drawable.wkb193,R.drawable.wkb194,R.drawable.wkb195,R.drawable.wkb196,R.drawable.wkb197,R.drawable.wkb198,R.drawable.wkb199,R.drawable.wkb200,R.drawable.wkb201,R.drawable.wkb202,R.drawable.wkb203,R.drawable.wkb204,R.drawable.wkb205,R.drawable.wkb206,R.drawable.wkb207,R.drawable.wkb208,R.drawable.wkb209,R.drawable.wkb210,R.drawable.wkb211,R.drawable.wkb212,R.drawable.wkb213,R.drawable.wkb214,R.drawable.wkb215,R.drawable.wkb216,R.drawable.wkb217,R.drawable.wkb218,R.drawable.wkb219,R.drawable.wkb220,R.drawable.wkb221,R.drawable.wkb222,R.drawable.wkb223,R.drawable.wkb224,R.drawable.wkb225,R.drawable.wkb226,R.drawable.wkb227,R.drawable.wkb228,R.drawable.wkb229,R.drawable.wkb230,R.drawable.wkb231,R.drawable.wkb232,R.drawable.wkb233,R.drawable.wkb234,R.drawable.wkb235,R.drawable.wkb236,R.drawable.wkb237,R.drawable.wkb238,R.drawable.wkb239,R.drawable.wkb240,R.drawable.wkb241,R.drawable.wkb242,R.drawable.wkb243,R.drawable.wkb244,R.drawable.wkb245,R.drawable.wkb246,R.drawable.wkb247,R.drawable.wkb248,R.drawable.wkb249,R.drawable.wkb250,R.drawable.wkb251,R.drawable.wkb252,R.drawable.wkb253,R.drawable.wkb254,R.drawable.wkb255,R.drawable.wkb256,R.drawable.wkb257,R.drawable.wkb258,R.drawable.wkb259,R.drawable.wkb260,R.drawable.wkb261,R.drawable.wkb262,R.drawable.wkb263,R.drawable.wkb264,R.drawable.wkb265,R.drawable.wkb266,R.drawable.wkb267,R.drawable.wkb268,R.drawable.wkb269,R.drawable.wkb270,R.drawable.wkb271,R.drawable.wkb272,R.drawable.wkb273,R.drawable.wkb274,R.drawable.wkb275,R.drawable.wkb276,R.drawable.wkb277,R.drawable.wkb278,R.drawable.wkb279,R.drawable.wkb280,R.drawable.wkb281,R.drawable.wkb282,R.drawable.wkb283,R.drawable.wkb284,R.drawable.wkb285,R.drawable.wkb286,R.drawable.wkb287,R.drawable.wkb288,R.drawable.wkb289,R.drawable.wkb290,R.drawable.wkb291,R.drawable.wkb292,R.drawable.wkb293,R.drawable.wkb294,R.drawable.wkb295,R.drawable.wkb296,R.drawable.wkb297,R.drawable.wkb298,R.drawable.wkb299,R.drawable.wkb300,R.drawable.wkb301,R.drawable.wkb302,R.drawable.wkb303,R.drawable.wkb304,R.drawable.wkb305,R.drawable.wkb306,R.drawable.wkb307,R.drawable.wkb308,R.drawable.wkb309,R.drawable.wkb310,R.drawable.wkb311,R.drawable.wkb312,R.drawable.wkb313,R.drawable.wkb314,R.drawable.wkb315,R.drawable.wkb316,R.drawable.wkb317,R.drawable.wkb318,R.drawable.wkb319,R.drawable.wkb320,R.drawable.wkb321,R.drawable.wkb322,R.drawable.wkb323,R.drawable.wkb324,R.drawable.wkb325,R.drawable.wkb326,R.drawable.wkb327,R.drawable.wkb328,R.drawable.wkb329,R.drawable.wkb330,R.drawable.wkb331,R.drawable.wkb332,R.drawable.wkb333,R.drawable.wkb334,R.drawable.wkb335,R.drawable.wkb336,R.drawable.wkb337,R.drawable.wkb338,R.drawable.wkb339,R.drawable.wkb340,R.drawable.wkb341,R.drawable.wkb342,R.drawable.wkb343,R.drawable.wkb344,R.drawable.wkb345,R.drawable.wkb346,R.drawable.wkb347,R.drawable.wkb348,R.drawable.wkb349,R.drawable.wkb350,R.drawable.wkb351,R.drawable.wkb352,R.drawable.wkb353,R.drawable.wkb354,R.drawable.wkb355,R.drawable.wkb356,R.drawable.wkb357,R.drawable.wkb358,R.drawable.wkb359,R.drawable.wkb360,R.drawable.wkb361,R.drawable.wkb362,R.drawable.wkb363,R.drawable.wkb364,R.drawable.wkb365,R.drawable.wkb366,R.drawable.wkb367,R.drawable.wkb368,R.drawable.wkb369,R.drawable.wkb370,R.drawable.wkb371,R.drawable.wkb372,R.drawable.wkb373,R.drawable.wkb374,R.drawable.wkb375,R.drawable.wkb376,R.drawable.wkb377,R.drawable.wkb378,R.drawable.wkb379,R.drawable.wkb380,R.drawable.wkb381,R.drawable.wkb382,R.drawable.wkb383,R.drawable.wkb384,R.drawable.wkb385,R.drawable.wkb386,R.drawable.wkb387,R.drawable.wkb388,R.drawable.wkb389,R.drawable.wkb390,R.drawable.wkb391,R.drawable.wkb392,R.drawable.wkb393,R.drawable.wkb394,R.drawable.wkb395,R.drawable.wkb396,R.drawable.wkb397,R.drawable.wkb398,R.drawable.wkb399,R.drawable.wkb400,R.drawable.wkb401,R.drawable.wkb402,R.drawable.wkb403,R.drawable.wkb404,R.drawable.wkb405,R.drawable.wkb406,R.drawable.wkb407,R.drawable.wkb408,R.drawable.wkb409,R.drawable.wkb410,R.drawable.wkb411,R.drawable.wkb412,R.drawable.wkb413,R.drawable.wkb414,R.drawable.wkb415,R.drawable.wkb416,R.drawable.wkb417,R.drawable.wkb418,R.drawable.wkb419,R.drawable.wkb420,R.drawable.wkb421,R.drawable.wkb422,R.drawable.wkb423,R.drawable.wkb424,R.drawable.wkb425,R.drawable.wkb426,R.drawable.wkb427,R.drawable.wkb428,R.drawable.wkb429,R.drawable.wkb430,R.drawable.wkb431,R.drawable.wkb432,R.drawable.wkb433,R.drawable.wkb434,R.drawable.wkb435,R.drawable.wkb436,R.drawable.wkb437,R.drawable.wkb438,R.drawable.wkb439,R.drawable.wkb440,R.drawable.wkb441,R.drawable.wkb442,R.drawable.wkb443,R.drawable.wkb444,R.drawable.wkb445,R.drawable.wkb446,R.drawable.wkb447,R.drawable.wkb448,R.drawable.wkb449,R.drawable.wkb450,R.drawable.wkb451,R.drawable.wkb452,R.drawable.wkb453,R.drawable.wkb454,R.drawable.wkb455,R.drawable.wkb456,R.drawable.wkb457,R.drawable.wkb458,R.drawable.wkb459,R.drawable.wkb460,R.drawable.wkb461,R.drawable.wkb462,R.drawable.wkb463,R.drawable.wkb464,R.drawable.wkb465,R.drawable.wkb466,R.drawable.wkb467,R.drawable.wkb468,R.drawable.wkb469,R.drawable.wkb470,R.drawable.wkb471,R.drawable.wkb472,R.drawable.wkb473,R.drawable.wkb474,R.drawable.wkb475,R.drawable.wkb476,R.drawable.wkb477,R.drawable.wkb478,R.drawable.wkb479,R.drawable.wkb480,R.drawable.wkb481,R.drawable.wkb482,R.drawable.wkb483,R.drawable.wkb484,R.drawable.wkb485,R.drawable.wkb486,R.drawable.wkb487,R.drawable.wkb488,R.drawable.wkb489,R.drawable.wkb490,R.drawable.wkb491,R.drawable.wkb492,R.drawable.wkb493,R.drawable.wkb494,R.drawable.wkb495,R.drawable.wkb496,R.drawable.wkb497,R.drawable.wkb498,R.drawable.wkb499,R.drawable.wkb500,R.drawable.wkb501,R.drawable.wkb502,R.drawable.wkb503,R.drawable.wkb504,R.drawable.wkb505,R.drawable.wkb506,R.drawable.wkb507,R.drawable.wkb508,R.drawable.wkb509,R.drawable.wkb510,R.drawable.wkb511,R.drawable.wkb512,R.drawable.wkb513,R.drawable.wkb514,R.drawable.wkb515,R.drawable.wkb516,R.drawable.wkb517,R.drawable.wkb518,R.drawable.wkb519,R.drawable.wkb520,R.drawable.wkb521,R.drawable.wkb522,R.drawable.wkb523,R.drawable.wkb524,R.drawable.wkb525,R.drawable.wkb526,R.drawable.wkb527,R.drawable.wkb528,R.drawable.wkb529,R.drawable.wkb530,R.drawable.wkb531,R.drawable.wkb532,R.drawable.wkb533,R.drawable.wkb534,R.drawable.wkb535,R.drawable.wkb536,R.drawable.wkb537,R.drawable.wkb538,R.drawable.wkb539,R.drawable.wkb540,R.drawable.wkb541,R.drawable.wkb542,R.drawable.wkb543,R.drawable.wkb544,R.drawable.wkb545,R.drawable.wkb546,R.drawable.wkb547,R.drawable.wkb548,R.drawable.wkb549,R.drawable.wkb550,R.drawable.wkb551,R.drawable.wkb552,R.drawable.wkb553,R.drawable.wkb554,R.drawable.wkb555,R.drawable.wkb556,R.drawable.wkb557,R.drawable.wkb558,R.drawable.wkb559,R.drawable.wkb560,R.drawable.wkb561,R.drawable.wkb562,R.drawable.wkb563,R.drawable.wkb564,R.drawable.wkb565,R.drawable.wkb566,R.drawable.wkb567,R.drawable.wkb568,R.drawable.wkb569,R.drawable.wkb570,R.drawable.wkb571,R.drawable.wkb572,R.drawable.wkb573,R.drawable.wkb574,R.drawable.wkb575,R.drawable.wkb576,R.drawable.wkb577,R.drawable.wkb578,R.drawable.wkb579,R.drawable.wkb580,R.drawable.wkb581,R.drawable.wkb582,R.drawable.wkb583,R.drawable.wkb584,R.drawable.wkb585,R.drawable.wkb586,R.drawable.wkb587,R.drawable.wkb588,R.drawable.wkb589,R.drawable.wkb590,R.drawable.wkb591,R.drawable.wkb592,R.drawable.wkb593,R.drawable.wkb594,R.drawable.wkb595,R.drawable.wkb596,R.drawable.wkb597,R.drawable.wkb598,R.drawable.wkb599,R.drawable.wkb600,R.drawable.wkb601,R.drawable.wkb602,R.drawable.wkb603,R.drawable.wkb604,R.drawable.wkb605,R.drawable.wkb606,R.drawable.wkb607,R.drawable.wkb608,R.drawable.wkb609,R.drawable.wkb610,R.drawable.wkb611,R.drawable.wkb612,R.drawable.wkb613,R.drawable.wkb614,R.drawable.wkb615,R.drawable.wkb616,R.drawable.wkb617,R.drawable.wkb618,R.drawable.wkb619,R.drawable.wkb620,R.drawable.wkb621,R.drawable.wkb622,R.drawable.wkb623,R.drawable.wkb624,R.drawable.wkb625,R.drawable.wkb626,R.drawable.wkb627,R.drawable.wkb628,R.drawable.wkb629,R.drawable.wkb630,R.drawable.wkb631,R.drawable.wkb632,R.drawable.wkb633,R.drawable.wkb634,R.drawable.wkb635,R.drawable.wkb636,R.drawable.wkb637,R.drawable.wkb638,R.drawable.wkb639,R.drawable.wkb640,R.drawable.wkb641,R.drawable.wkb642,R.drawable.wkb643,R.drawable.wkb644,R.drawable.wkb645,R.drawable.wkb646,R.drawable.wkb647,R.drawable.wkb648,R.drawable.wkb649,R.drawable.wkb650,R.drawable.wkb651,R.drawable.wkb652,R.drawable.wkb653,R.drawable.wkb654,R.drawable.wkb655,R.drawable.wkb656,R.drawable.wkb657,R.drawable.wkb658,R.drawable.wkb659,R.drawable.wkb660,R.drawable.wkb661,R.drawable.wkb662,R.drawable.wkb663,R.drawable.wkb664,R.drawable.wkb665,R.drawable.wkb666,R.drawable.wkb667,R.drawable.wkb668,R.drawable.wkb669,R.drawable.wkb670,R.drawable.wkb671,R.drawable.wkb672,R.drawable.wkb673,R.drawable.wkb674,R.drawable.wkb675,R.drawable.wkb676,R.drawable.wkb677,R.drawable.wkb678,R.drawable.wkb679,R.drawable.wkb680,R.drawable.wkb681,R.drawable.wkb682,R.drawable.wkb683,R.drawable.wkb684,R.drawable.wkb685,R.drawable.wkb686,R.drawable.wkb687,R.drawable.wkb688,R.drawable.wkb689,R.drawable.wkb690,R.drawable.wkb691,R.drawable.wkb692,R.drawable.wkb693,R.drawable.wkb694,R.drawable.wkb695,R.drawable.wkb696,R.drawable.wkb697,R.drawable.wkb698,R.drawable.wkb699,R.drawable.wkb700,R.drawable.wkb701,R.drawable.wkb702,R.drawable.wkb703,R.drawable.wkb704,R.drawable.wkb705,R.drawable.wkb706,R.drawable.wkb707,R.drawable.wkb708,R.drawable.wkb709,R.drawable.wkb710,R.drawable.wkb711,R.drawable.wkb712,R.drawable.wkb713,R.drawable.wkb714,R.drawable.wkb715,R.drawable.wkb716,R.drawable.wkb717,R.drawable.wkb718,R.drawable.wkb719,R.drawable.wkb720,R.drawable.wkb721,R.drawable.wkb722,R.drawable.wkb723,R.drawable.wkb724,R.drawable.wkb725,R.drawable.wkb726,R.drawable.wkb727,R.drawable.wkb728,R.drawable.wkb729,R.drawable.wkb730,R.drawable.wkb731,R.drawable.wkb732,R.drawable.wkb733,R.drawable.wkb734,R.drawable.wkb735,R.drawable.wkb736,R.drawable.wkb737,R.drawable.wkb738,R.drawable.wkb739,R.drawable.wkb740,R.drawable.wkb741,R.drawable.wkb742,R.drawable.wkb743,R.drawable.wkb744,R.drawable.wkb745,R.drawable.wkb746,R.drawable.wkb747,R.drawable.wkb748,R.drawable.wkb749,R.drawable.wkb750,R.drawable.wkb751,R.drawable.wkb752,R.drawable.wkb753,R.drawable.wkb754,R.drawable.wkb755,R.drawable.wkb756,R.drawable.wkb757,R.drawable.wkb758,R.drawable.wkb759,R.drawable.wkb760,R.drawable.wkb761,R.drawable.wkb762,R.drawable.wkb763,R.drawable.wkb764,R.drawable.wkb765,R.drawable.wkb766,R.drawable.wkb767,R.drawable.wkb768,R.drawable.wkb769,R.drawable.wkb770,R.drawable.wkb771,R.drawable.wkb772,R.drawable.wkb773,R.drawable.wkb774,R.drawable.wkb775,R.drawable.wkb776,R.drawable.wkb777,R.drawable.wkb778,R.drawable.wkb779,R.drawable.wkb780,R.drawable.wkb781,R.drawable.wkb782,R.drawable.wkb783,R.drawable.wkb784,R.drawable.wkb785,R.drawable.wkb786,R.drawable.wkb787,R.drawable.wkb788,R.drawable.wkb789,R.drawable.wkb790,R.drawable.wkb791,R.drawable.wkb792,R.drawable.wkb793,R.drawable.wkb794,R.drawable.wkb795,R.drawable.wkb796,R.drawable.wkb797,R.drawable.wkb798,R.drawable.wkb799,R.drawable.wkb800,R.drawable.wkb801,R.drawable.wkb802,R.drawable.wkb803,R.drawable.wkb804,R.drawable.wkb805,R.drawable.wkb806,R.drawable.wkb807,R.drawable.wkb808,R.drawable.wkb809,R.drawable.wkb810,R.drawable.wkb811,R.drawable.wkb812,R.drawable.wkb813,R.drawable.wkb814,R.drawable.wkb815,R.drawable.wkb816,R.drawable.wkb817,R.drawable.wkb818,R.drawable.wkb819,R.drawable.wkb820,R.drawable.wkb821,R.drawable.wkb822,R.drawable.wkb823,R.drawable.wkb824,R.drawable.wkb825,R.drawable.wkb826,R.drawable.wkb827,R.drawable.wkb828,R.drawable.wkb829,R.drawable.wkb830,R.drawable.wkb831,R.drawable.wkb832,R.drawable.wkb833,R.drawable.wkb834,R.drawable.wkb835,R.drawable.wkb836,R.drawable.wkb837,R.drawable.wkb838,R.drawable.wkb839,R.drawable.wkb840,R.drawable.wkb841,R.drawable.wkb842,R.drawable.wkb843,R.drawable.wkb844,R.drawable.wkb845,R.drawable.wkb846,R.drawable.wkb847,R.drawable.wkb848,R.drawable.wkb849,R.drawable.wkb850,R.drawable.wkb851,R.drawable.wkb852,R.drawable.wkb853,R.drawable.wkb854,R.drawable.wkb855,R.drawable.wkb856,R.drawable.wkb857,R.drawable.wkb858,R.drawable.wkb859,R.drawable.wkb860,R.drawable.wkb861,R.drawable.wkb862,R.drawable.wkb863,R.drawable.wkb864,R.drawable.wkb865,R.drawable.wkb866,R.drawable.wkb867,R.drawable.wkb868,R.drawable.wkb869,R.drawable.wkb870,R.drawable.wkb871,R.drawable.wkb872,R.drawable.wkb873,R.drawable.wkb874,R.drawable.wkb875,R.drawable.wkb876,R.drawable.wkb877,R.drawable.wkb878,R.drawable.wkb879,R.drawable.wkb880,R.drawable.wkb881,R.drawable.wkb882,R.drawable.wkb883,R.drawable.wkb884,R.drawable.wkb885,R.drawable.wkb886,R.drawable.wkb887,R.drawable.wkb888,R.drawable.wkb889,R.drawable.wkb890,R.drawable.wkb891,R.drawable.wkb892,R.drawable.wkb893,R.drawable.wkb894,R.drawable.wkb895,R.drawable.wkb896,R.drawable.wkb897,R.drawable.wkb898,R.drawable.wkb899,R.drawable.wkb900,R.drawable.wkb901,R.drawable.wkb902,R.drawable.wkb903,R.drawable.wkb904,R.drawable.wkb905,R.drawable.wkb906,R.drawable.wkb907,R.drawable.wkb908,R.drawable.wkb909,R.drawable.wkb910,R.drawable.wkb911,R.drawable.wkb912,R.drawable.wkb913,R.drawable.wkb914,R.drawable.wkb915,R.drawable.wkb916,R.drawable.wkb917,R.drawable.wkb918,R.drawable.wkb919,R.drawable.wkb920,R.drawable.wkb921,R.drawable.wkb922,R.drawable.wkb923,R.drawable.wkb924,R.drawable.wkb925,R.drawable.wkb926,R.drawable.wkb927,R.drawable.wkb928,R.drawable.wkb929,R.drawable.wkb930,R.drawable.wkb931,R.drawable.wkb932,R.drawable.wkb933,R.drawable.wkb934,R.drawable.wkb935,R.drawable.wkb936,R.drawable.wkb937,R.drawable.wkb938,R.drawable.wkb939,R.drawable.wkb940,R.drawable.wkb941,R.drawable.wkb942,R.drawable.wkb943,R.drawable.wkb944,R.drawable.wkb945,R.drawable.wkb946,R.drawable.wkb947,R.drawable.wkb948,R.drawable.wkb949,R.drawable.wkb950,R.drawable.wkb951,R.drawable.wkb952,R.drawable.wkb953,R.drawable.wkb954,R.drawable.wkb955,R.drawable.wkb956,R.drawable.wkb957,R.drawable.wkb958,R.drawable.wkb959,R.drawable.wkb960,R.drawable.wkb961,R.drawable.wkb962,R.drawable.wkb963,R.drawable.wkb964,R.drawable.wkb965,R.drawable.wkb966,R.drawable.wkb967,R.drawable.wkb968,R.drawable.wkb969,R.drawable.wkb970,R.drawable.wkb971,R.drawable.wkb972,R.drawable.wkb973,R.drawable.wkb974,R.drawable.wkb975,R.drawable.wkb976,R.drawable.wkb977,R.drawable.wkb978,R.drawable.wkb979,R.drawable.wkb980,R.drawable.wkb981,R.drawable.wkb982,R.drawable.wkb983,R.drawable.wkb984,R.drawable.wkb985,R.drawable.wkb986,R.drawable.wkb987,R.drawable.wkb988,R.drawable.wkb989,R.drawable.wkb990,R.drawable.wkb991,R.drawable.wkb992,R.drawable.wkb993,R.drawable.wkb994,R.drawable.wkb995,R.drawable.wkb996,R.drawable.wkb997,R.drawable.wkb998,R.drawable.wkb999};
    private final static int[] SPEED_NOTIF_ICONS_WMB = {R.drawable.wmb010,R.drawable.wmb011,R.drawable.wmb012,R.drawable.wmb013,R.drawable.wmb014,R.drawable.wmb015,R.drawable.wmb016,R.drawable.wmb017,R.drawable.wmb018,R.drawable.wmb019,R.drawable.wmb020,R.drawable.wmb021,R.drawable.wmb022,R.drawable.wmb023,R.drawable.wmb024,R.drawable.wmb025,R.drawable.wmb026,R.drawable.wmb027,R.drawable.wmb028,R.drawable.wmb029,R.drawable.wmb030,R.drawable.wmb031,R.drawable.wmb032,R.drawable.wmb033,R.drawable.wmb034,R.drawable.wmb035,R.drawable.wmb036,R.drawable.wmb037,R.drawable.wmb038,R.drawable.wmb039,R.drawable.wmb040,R.drawable.wmb041,R.drawable.wmb042,R.drawable.wmb043,R.drawable.wmb044,R.drawable.wmb045,R.drawable.wmb046,R.drawable.wmb047,R.drawable.wmb048,R.drawable.wmb049,R.drawable.wmb050,R.drawable.wmb051,R.drawable.wmb052,R.drawable.wmb053,R.drawable.wmb054,R.drawable.wmb055,R.drawable.wmb056,R.drawable.wmb057,R.drawable.wmb058,R.drawable.wmb059,R.drawable.wmb060,R.drawable.wmb061,R.drawable.wmb062,R.drawable.wmb063,R.drawable.wmb064,R.drawable.wmb065,R.drawable.wmb066,R.drawable.wmb067,R.drawable.wmb068,R.drawable.wmb069,R.drawable.wmb070,R.drawable.wmb071,R.drawable.wmb072,R.drawable.wmb073,R.drawable.wmb074,R.drawable.wmb075,R.drawable.wmb076,R.drawable.wmb077,R.drawable.wmb078,R.drawable.wmb079,R.drawable.wmb080,R.drawable.wmb081,R.drawable.wmb082,R.drawable.wmb083,R.drawable.wmb084,R.drawable.wmb085,R.drawable.wmb086,R.drawable.wmb087,R.drawable.wmb088,R.drawable.wmb089,R.drawable.wmb090,R.drawable.wmb091,R.drawable.wmb092,R.drawable.wmb093,R.drawable.wmb094,R.drawable.wmb095,R.drawable.wmb096,R.drawable.wmb097,R.drawable.wmb098,R.drawable.wmb099,R.drawable.wmb100,R.drawable.wmb101,R.drawable.wmb102,R.drawable.wmb103,R.drawable.wmb104,R.drawable.wmb105,R.drawable.wmb106,R.drawable.wmb107,R.drawable.wmb108,R.drawable.wmb109,R.drawable.wmb110,R.drawable.wmb111,R.drawable.wmb112,R.drawable.wmb113,R.drawable.wmb114,R.drawable.wmb115,R.drawable.wmb116,R.drawable.wmb117,R.drawable.wmb118,R.drawable.wmb119,R.drawable.wmb120,R.drawable.wmb121,R.drawable.wmb122,R.drawable.wmb123,R.drawable.wmb124,R.drawable.wmb125,R.drawable.wmb126,R.drawable.wmb127,R.drawable.wmb128,R.drawable.wmb129,R.drawable.wmb130,R.drawable.wmb131,R.drawable.wmb132,R.drawable.wmb133,R.drawable.wmb134,R.drawable.wmb135,R.drawable.wmb136,R.drawable.wmb137,R.drawable.wmb138,R.drawable.wmb139,R.drawable.wmb140,R.drawable.wmb141,R.drawable.wmb142,R.drawable.wmb143,R.drawable.wmb144,R.drawable.wmb145,R.drawable.wmb146,R.drawable.wmb147,R.drawable.wmb148,R.drawable.wmb149,R.drawable.wmb150,R.drawable.wmb151,R.drawable.wmb152,R.drawable.wmb153,R.drawable.wmb154,R.drawable.wmb155,R.drawable.wmb156,R.drawable.wmb157,R.drawable.wmb158,R.drawable.wmb159,R.drawable.wmb160,R.drawable.wmb161,R.drawable.wmb162,R.drawable.wmb163,R.drawable.wmb164,R.drawable.wmb165,R.drawable.wmb166,R.drawable.wmb167,R.drawable.wmb168,R.drawable.wmb169,R.drawable.wmb170,R.drawable.wmb171,R.drawable.wmb172,R.drawable.wmb173,R.drawable.wmb174,R.drawable.wmb175,R.drawable.wmb176,R.drawable.wmb177,R.drawable.wmb178,R.drawable.wmb179,R.drawable.wmb180,R.drawable.wmb181,R.drawable.wmb182,R.drawable.wmb183,R.drawable.wmb184,R.drawable.wmb185,R.drawable.wmb186,R.drawable.wmb187,R.drawable.wmb188,R.drawable.wmb189,R.drawable.wmb190};

    private static NetworkManager sInstance;
    private Context mContext;
    private SharedPreferences mPref;

    private long oldTotalRxBytes = 0L;
    private long oldTotalTxBytes = 0L;
    private long oldTime;

    private float outputSpeed = 0f;
    private float outputRxSpeed = 0f;
    private float outputTxSpeed = 0f;

    private float outputBlow = 0f;

    private static final int MSG_UPDATE = 1;

    private List<AppInfo> mAppInfos = new ArrayList<>();
    private List<DataChangeListener> mDataChangeListeners = new ArrayList<DataChangeListener>();
    private List<AppDataChangeListener> mAppDataChangeListeners = new ArrayList<AppDataChangeListener>();
    private Handler mMainThreadHandler = new Handler();
    private HandlerThread mThread = new HandlerThread("network_speed");
    private NetworkHandler mHandler;
    private LinkedList<Float> mSpeedCache = new LinkedList<Float>();
    private ConcurrentHashMap<String, Bitmap> mAppIcons = new ConcurrentHashMap<>();

    private class NetworkHandler extends Handler{

        public NetworkHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE:
                    update();
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE, getFreshRate());
                    break;
            }
        }
    }

    private Comparator<AppInfo> mComparator = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo lhs, AppInfo rhs) {
            return (int)(rhs.getSpeed() - lhs.getSpeed());
        }
    };

    public interface DataChangeListener{
        void onDataChanged(float speed, float rxSpeed, float txSpeed);
    }

    public interface AppDataChangeListener{
        void onAppDataChanged(List<AppInfo> appInfos);
    }

    private NetworkManager(Context context){
        mContext = context;
        new Thread(){
            @Override
            public void run() {
                super.run();
                mAppInfos = initAppInfos();
            }
        }.start();
        mPref = PreferenceUtils.getInstance().getDefault();
        mThread.start();
        mHandler = new NetworkHandler(mThread.getLooper());
    }

    public static NetworkManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new NetworkManager(context);
        }
        return sInstance;
    }

    public void addListener(DataChangeListener listener){
        if (!mDataChangeListeners.contains(listener)){
            mDataChangeListeners.add(listener);
        }
        mHandler.removeMessages(MSG_UPDATE);
        mHandler.sendEmptyMessage(MSG_UPDATE);
    }

    public void removeListener(DataChangeListener listener){
        mDataChangeListeners.remove(listener);
        if (mDataChangeListeners.size() == 0 && mAppDataChangeListeners.size() == 0){
            mHandler.removeMessages(MSG_UPDATE);
        }
    }


    public void addAppListener(AppDataChangeListener listener){
        if (!mAppDataChangeListeners.contains(listener)){
            mAppDataChangeListeners.add(listener);
        }
        mHandler.removeMessages(MSG_UPDATE);
        mHandler.sendEmptyMessage(MSG_UPDATE);
    }

    public void removeAppListener(AppDataChangeListener listener){
        mAppDataChangeListeners.remove(listener);
        if (mDataChangeListeners.size() == 0 && mAppDataChangeListeners.size() == 0){
            mHandler.removeMessages(MSG_UPDATE);
        }
    }

    public void setFreshRate(int rate){
        mPref.edit().putInt("fresh_rate", rate).commit();
    }

    public int getFreshRate(){
        return mPref.getInt("fresh_rate", 3000);
    }

    public int getSpeedIcon(){
        float speed = getSpeed();
        int resId = 0;
        if(speed < 1000){   // b/s
            resId = SPEED_NOTIF_ICONS_WKB[0];
        }else if(speed < 1000000){  // kb/s
            resId = SPEED_NOTIF_ICONS_WKB[(int)(speed/1000)];
        }else if(speed < 1000000000){   // mb/s
            resId = SPEED_NOTIF_ICONS_WMB[(int)(speed/100000) - 10];
        }
        return resId;
    }

    public void stop(){
        mHandler.removeMessages(MSG_UPDATE);
    }

    public void start(){
        mHandler.removeMessages(MSG_UPDATE);
        mHandler.sendEmptyMessage(MSG_UPDATE);
    }

    private void update(){
        long newTxBytes = TrafficStats.getTotalTxBytes();
        long newRxBytes = TrafficStats.getTotalRxBytes();
        long newTime = System.currentTimeMillis();

        outputBlow = newTxBytes + newRxBytes;
        try {
            outputTxSpeed = (newTxBytes - oldTotalTxBytes)*1000/(newTime - oldTime);
            outputRxSpeed = (newRxBytes - oldTotalRxBytes)*1000/(newTime - oldTime);
            outputSpeed = outputRxSpeed + outputTxSpeed; // b/s

            addSpeedCache(outputSpeed);
        } catch (Exception e) {
            e.printStackTrace();
        }

        oldTotalRxBytes = newRxBytes;
        oldTotalTxBytes = newTxBytes;
        oldTime = newTime;

        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                for (DataChangeListener listener : mDataChangeListeners) {
                    listener.onDataChanged(outputSpeed, outputRxSpeed, outputTxSpeed);
                }
            }
        });

        if (mAppDataChangeListeners.size() > 0){
            for (AppInfo info : mAppInfos){
                info.update();
            }

            Collections.sort(mAppInfos, mComparator);

            mMainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (AppDataChangeListener listener : mAppDataChangeListeners){
                        listener.onAppDataChanged(mAppInfos);
                    }
                }
            });
        }
    }

    public float getBlow(){
        return outputBlow;
    }

    public float getSpeed(){
        return outputSpeed;
    }

    public float getRxSpeed(){
        return outputRxSpeed;
    }

    public float getTxSpeed(){
        return outputTxSpeed;
    }

    public static String formatSpeed(float speed){
        String res = "";
        if(speed < UNIT_KB){   // b/s
            res = "B/s";
        }else if(speed < UNIT_MB){  // kb/s
            speed = speed/UNIT_KB;
            res = "K/s";
        }else if(speed < UNIT_GB){   // mb/s
            speed = speed/UNIT_MB;
            res = "M/s";
        }
        res = formatNumber(speed) + res;
        return res;
    }

    public static String formatBlow(float blow){
        String res = "";
        if(blow < UNIT_KB){   // b/s
            res = formatNumber(blow) + "B";
        }else if(blow < UNIT_MB){  // kb/s
            blow = blow/UNIT_KB;
            res = formatNumber(blow) + "K";
        }else if(blow < UNIT_GB){   // mb/s
            blow = blow/UNIT_MB;
            res = formatNumber2(blow) + "M";
        }else if (blow < UNIT_TB){
            blow = blow/UNIT_GB;
            res = formatNumber2(blow) + "G";
        }
        return res;
    }

    private static String formatNumber(float num){
        DecimalFormat df = new DecimalFormat("0");
        return df.format(num);
    }

    private static String formatNumber2(float num){
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(num);
    }

    public List<AppInfo> getAppInfos(){
        return mAppInfos;
    }

    public List<Float> getSpeedCache(){
        return mSpeedCache;
    }

    private void addSpeedCache(float speed){
        if (mSpeedCache.size() >= SPEED_CACHE_MAX){
            mSpeedCache.removeLast();
        }
        mSpeedCache.push(speed);
    }

    private List<AppInfo> initAppInfos() {
        List<AppInfo> uidList = new ArrayList<AppInfo>();
        PackageManager pm = mContext.getPackageManager();
        List<PackageInfo> packageinfos = pm
                .getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES
                        | PackageManager.GET_PERMISSIONS);
        for (PackageInfo info : packageinfos) {
            String[] permissions = info.requestedPermissions;
            if (permissions != null && permissions.length > 0) {
                for (String permission : permissions) {
                    if ("android.permission.INTERNET".equals(permission)) {
                        AppInfo appInfo = new AppInfo();
                        appInfo.uid = info.applicationInfo.uid;
                        appInfo.appName = pm.getApplicationLabel(info.applicationInfo).toString();
                        appInfo.pkgName = info.packageName;
                        appInfo.originRxBlow = TrafficStats.getUidRxBytes(appInfo.uid);
                        appInfo.originTxBlow = TrafficStats.getUidTxBytes(appInfo.uid);

                        Drawable icon = null;
                        try {
                            ApplicationInfo applicationInfo = pm.getApplicationInfo(appInfo.pkgName, 0);
                            icon = applicationInfo.loadIcon(pm);
                        } catch (PackageManager.NameNotFoundException e) {
                            icon = App.getContext().getResources().getDrawable(android.R.drawable.sym_def_app_icon);
                        }
                        Bitmap bitmapIcon = null;
                        if (icon instanceof BitmapDrawable){
                            bitmapIcon = ((BitmapDrawable) icon).getBitmap();
                        }else{
                            bitmapIcon = drawableToBitamp(icon);
                        }

                        mAppIcons.put(appInfo.pkgName, bitmapIcon);

                        uidList.add(appInfo);
                    }
                }
            }
        }

        return uidList;
    }

    private Bitmap drawableToBitamp(Drawable drawable)
    {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w,h,config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    public Bitmap getAppIcon(String pkgName){
        return mAppIcons.get(pkgName);
    }
}
