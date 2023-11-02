package com.dreamteam.sharedream.view

import android.graphics.PointF
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.dreamteam.sharedream.R
import com.dreamteam.sharedream.Util.Constants
import com.dreamteam.sharedream.databinding.FragmentMapViewBinding
import com.dreamteam.sharedream.model.LocationData
import com.dreamteam.sharedream.model.PostRcv
import com.dreamteam.sharedream.viewmodel.MyPostFeedViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import java.util.Locale


class MapViewFragment : Fragment(), OnMapReadyCallback {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    private var _binding: FragmentMapViewBinding? = null
    private val binding get() = _binding!!

    private lateinit var locationSource: FusedLocationSource

    private lateinit var naverMap: NaverMap

    private val myPostFeedViewModel: MyPostFeedViewModel by activityViewModels()

    private var _locationInfo: LocationData? = null
    private val locationInfo : LocationData get() = _locationInfo!!

    private var _currentPostInfo: PostRcv? = null
    private val currentPostInfo : PostRcv get() = _currentPostInfo!!

    private val locationCityInfo: MutableList<String> = mutableListOf()

    private var initCameraPosition: LatLng = LatLng(37.655798, 126.7748480)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapViewBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // NaverMap 인스턴스 생성
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }
        mapFragment.getMapAsync(this)

        myPostFeedViewModel.currentPost.observe(viewLifecycleOwner) {
            it?.let {
                // currentPostInfo 객체 초기화
                _currentPostInfo = it

                // 게시글에 설정된 LatLng에 따라 카메라 초기 위치 설정
                if (it.locationLatLng.isNotEmpty()) {
                    initCameraPosition = LatLng(it.locationLatLng[0], it.locationLatLng[1])
                }

                // 게시글 작성자가 아니면 마커 위치 변경할 수 없도록 함.
                if (it.uid != Constants.currentUserUid) {
                    binding.mapBtnComplete.visibility = View.GONE
                } else {
                    binding.mapBtnComplete.visibility = View.VISIBLE
                }

                // 초기 마커 텍스트 위치 지정.
                binding.mapTvAddress.text = it.address
            }

            // 뒤로가기 버튼 클릭 이벤트
            binding.mapBtnBack.setOnClickListener {
                parentFragmentManager.popBackStack()
            }

            // 설정 완료 버튼 클릭 이벤트
            binding.mapBtnComplete.setOnClickListener {
                locationInfo?.let {
                    if (binding.mapTvAddress.text.length > 2) {
                        myPostFeedViewModel.setLocationInfo(locationInfo!!)
                        // 디테일 페이지로 돌아온 뒤 지도 클릭 시 변경사항 반영
                        myPostFeedViewModel.setCurrentPost(currentPostInfo.copy(locationLatLng = listOf(locationInfo.latLng.longitude,locationInfo.latLng.longitude)))
                        Log.d("xxxx", " setLocationInfo ! ${locationInfo} ")
                        parentFragmentManager.popBackStack()
                    }
                } ?: Toast.makeText(requireContext(), "위치를 다시 선택해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 지도 마커 옵션 지정 Todo 커스텀 이미지 마커
    private val marker = Marker()


    // NaverMap 객체 설정
    override fun onMapReady(naverMap: NaverMap) {
        Log.d("xxxx", "onMapReady: 생성 ")
        this.naverMap = naverMap

        // 마커 이미지 지정
        marker.icon = OverlayImage.fromResource(R.drawable.map_ic_marker_fill_red32)

        // 마커 색상 지정
        marker.icon = MarkerIcons.YELLOW

        // 마커 크기 지정
        marker.width = 50
        marker.height = 70
        // 아이콘 크기 자동 지정
//        marker.width = Marker.SIZE_AUTO
//        marker.height = Marker.SIZE_AUTO

        // 마커 텍스트
//        marker.captionText = "여기!"

        naverMap.apply {
            // Map에 나타나는 Layer 관리 ( 아래 예시 코드 -> 등산로, 등고선 제외 )
            setLayerGroupEnabled(NaverMap.LAYER_GROUP_MOUNTAIN, false)

            // 실내지도 속성 활성화 여부 -> 지도유형이 Basic, Terrain 일 경우에만 사용가능.
            isIndoorEnabled = true

            // 지도 밝기 조정 ( -1 ~ 1 사이로 지정 )
            lightness = 0.1f

            // 최소 최대 줌 레벨 제한
            maxZoom = 20.0
            minZoom = 8.0

            marker.position = LatLng(initCameraPosition.latitude, initCameraPosition.longitude)
            marker.map = naverMap
            // UI 설정 객체 접근
            val uiSettings = naverMap.uiSettings

            uiSettings.apply {
                // 좌측 하단 Naver 로고 클릭 활성화 여부
                isLogoClickEnabled = false

                // 나침반 활성화 여부
                isCompassEnabled = true

                // 현재 위치 추적 버튼 활성화 여부 -> 별도로 사용자 위치 설정을 해주어야 한다, 객체 생성할 때 Option으로 넣어줄 수도 있다.
                isLocationButtonEnabled = true
            }

            // 지도 위치 클릭 이벤트
            currentPostInfo?.let {
                if (it.uid != Constants.currentUserUid) {
                    // todo 게시글 작성자가 아닌 사용자의 지도 클릭 이벤트
                } else {
                    setOnMapClickListener { pointF, latLng ->
                        mapClickEvent(latLng)
                    }
                }
            }

            // 게시글 작성 시 맵 클릭 이벤트
            setOnMapClickListener { pointF, latLng ->
                mapClickEvent(latLng)
            }


            // 카메라 초기 위치 지정, 애니메이션
            moveCamera(
                CameraUpdate.scrollTo(initCameraPosition)
                    .animate(CameraAnimation.Fly, 1000)
            )

            // 현재 위치
            locationSource =
                FusedLocationSource(this@MapViewFragment, LOCATION_PERMISSION_REQUEST_CODE)
            locationTrackingMode = LocationTrackingMode.NoFollow

        }
    }

    private fun mapClickEvent(latLng: LatLng) {
        marker.position = LatLng(latLng.latitude, latLng.longitude)
        marker.map = naverMap

        getAddress(latLng.latitude, latLng.longitude)
        Log.d("xxxx", " 지도 클릭 ${getAddress(latLng.latitude, latLng.longitude)}")
    }


    private fun getAddress(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(requireContext(), Locale.KOREA)
        val address: ArrayList<Address>
        var addressResult = ""
        locationCityInfo.clear()
        try {
            // API 레벨 33 이상일 경우
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(
                    latitude, longitude, 2
                )
                { address ->
                    if (address.size != 0) {
                        addressResult =
                            address[0].getAddressLine(0).replace("대한민국", "").replace("KR", "")
                        Log.d("xxxx", "getAddress 33 ↑ : ${address}")

                        binding.mapTvAddress.text = addressResult

                        for (index in address.indices) {
                            locationCityInfo.apply {
                                add(address[index].adminArea)
                                address[index].locality?.let {
                                    add(it)
                                }

                                address[index].featureName?.let {
                                    add(it)
                                }
                            }
                        }

                        _locationInfo = LocationData(
                            LatLng(latitude, longitude),
                            addressResult,
                            locationCityInfo
                        )
                        Log.d("xxxx", "location Info 33↑ Change: $locationInfo")
                    }
                }
            } else { // API 레벨 33 미만일 경우
                address = geocoder.getFromLocation(latitude, longitude, 1) as ArrayList<Address>
                if (address.size > 0) {
                    val currentLocationAddress = address[0].getAddressLine(0)
                        .toString()
                    addressResult = currentLocationAddress.replace("대한민국", "")

                    binding.mapTvAddress.text = addressResult

                    for (index in address.indices) {
                        locationCityInfo.apply {
                            add(address[index].adminArea)
                            add(address[index].locality)
                            add(address[index].featureName)
                        }
                    }

                    Log.d("xxxx", " 33 ↓ locationCityInfo 33 ↓= $locationCityInfo ")
                    _locationInfo = LocationData(
                        LatLng(latitude, longitude),
                        addressResult,
                        locationCityInfo
                    )
                    Log.d("xxxx", "getAddress33 ↓: ${address} ")
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return addressResult
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}