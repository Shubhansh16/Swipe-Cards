package com.example.topbottom

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

import com.example.topbottom.ui.theme.TopBottomTheme
import java.util.Locale.Builder
import java.util.stream.LongStream


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TopBottomTheme {
               Surface(
                   modifier = Modifier.fillMaxSize(),
                   color = MaterialTheme.colorScheme.background
               ) {
                   val pagerState = rememberPagerState (pageCount = { locations.count() })
                   Column (

                   ){
                       HorizontalPager(
                           state = pagerState,
                           modifier = Modifier
                               .weight(.7f)
                               .padding(top = 32.dp),
                           pageSpacing = 1.dp,
                           beyondBoundsPageCount = locations.count()
                       ){page->
                           Box(modifier = Modifier
                               .zIndex(page * 2f)
                               .padding(
                                   start = 64.dp,
                                   end = 32.dp,
                                   top = 33.dp,
                                   bottom = 33.dp
                               )
                               .graphicsLayer {
                                   val startOffset = pagerState.startOffsetForPage(page)
                                   translationX = size.width * (startOffset * .99f)

                                   alpha = (2f - startOffset) / 2

                                   val blur = (startOffset * 20).coerceAtLeast(.1f)
                                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                       renderEffect = RenderEffect
                                           .createBlurEffect(
                                               blur, blur, Shader.TileMode.DECAL
                                           )
                                           .asComposeRenderEffect()
                                   }

                                   val scale = 1f - (startOffset * .1f)
                                   scaleX = scale
                                   scaleY = scale

                               }
                               .clip(RoundedCornerShape(20.dp))){
                            Image(
                                painter = painterResource(id = locations[page].image),
                                contentDescription =null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                           }
                       }
                       Row (
                           modifier= Modifier
                               .padding(horizontal = 16.dp)
                               .fillMaxWidth()
                               .weight(.3f),
                           horizontalArrangement = Arrangement.SpaceBetween,
                           verticalAlignment = Alignment.CenterVertically
                       ){
                         val verticalState = rememberPagerState(pageCount = {
                             locations.count()
                         })
                           VerticalPager(state = verticalState, modifier = Modifier
                               .weight(1f)
                               .height(86.dp), userScrollEnabled = false, horizontalAlignment = Alignment.Start) {page->
                               Column(
                                   verticalArrangement = Arrangement.Center
                               ) {
                                 Text(text = locations[page].title,
                                     style = MaterialTheme.typography.headlineLarge.copy(
                                         fontWeight = FontWeight.Thin,
                                         fontSize = 28.sp
                                     )
                                 )
                                 Spacer(modifier = Modifier.height(8.dp))
                                   Text(text = locations[page].subTitle,
                                       style = MaterialTheme.typography.bodyLarge.copy(
                                           fontWeight = FontWeight.Bold,
                                           fontSize = 14.sp
                                       )
                                   )
                               }
                           }
                           LaunchedEffect(Unit) {
                               snapshotFlow {
                                   Pair(
                                       pagerState.currentPage,
                                       pagerState.currentPageOffsetFraction
                                   )
                               }.collect{(page, offset)->
                                   verticalState.scrollToPage(page,offset)
                               }
                           }
                       }
                   }
               }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun PagerState.offsetForPage(page:Int) = (currentPage-page)+currentPageOffsetFraction

@OptIn(ExperimentalFoundationApi::class)
fun PagerState.startOffsetForPage(page:Int)= offsetForPage(page).coerceAtLeast(0f)