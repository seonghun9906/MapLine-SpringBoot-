package kr.co.icia.mapline.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.icia.mapline.util.KakaoApiUtil;
import kr.co.icia.mapline.util.KakaoApiUtil.Point;

@Controller
public class MapController {

	/**
	 * 자동차 이동 경로 그리기
	 * 
	 * @param fromAddress 출발지 주소정보
	 * @param toAddress   목적지 주소정보
	 * @param model       html파일에 값을 전달해주는 객체
	 * @return html 파일위치
	 * 
	 */
	@GetMapping("/map/paths") // url : /map/paths
	public String getMapPaths(@RequestParam(required = false) String fromAddress, //
			@RequestParam(required = false) String toAddress, //
			Model model) throws IOException, InterruptedException {
		Point fromPoint = null;
		Point toPoint = null;
		if (fromAddress != null && !fromAddress.isEmpty()) {
			fromPoint = KakaoApiUtil.getPointByAddress(fromAddress);
			model.addAttribute("fromPoint", fromPoint);
		}
		if (toAddress != null && !toAddress.isEmpty()) {
			toPoint = KakaoApiUtil.getPointByAddress(toAddress);
			model.addAttribute("toPoint", toPoint);
		}

		if (fromPoint != null && toPoint != null) {
			List<Point> pointList = KakaoApiUtil.getVehiclePaths(fromPoint, toPoint);
			String pointListJson = new ObjectMapper().writer().writeValueAsString(pointList);
			System.out.println("pointListJson");
			System.out.println(pointListJson);
			model.addAttribute("pointList", pointListJson);
		}
		return "map/paths";
	}

	/**
	 * 주소를 좌표로 변환
	 * 
	 * @param address 주소정보
	 * @param model   html파일에 값을 전달해주는 객체
	 * @return html 파일위치
	 * 
	 */
	@GetMapping("/map/address/point") // url : /map/address/point
	public String getMapAddressPoint(@RequestParam(required = false) String address, Model model)
	// 파라미터 이름과 변수이름이 같으면 생략 가능 ▲ (RequestParam)
			throws IOException, InterruptedException {
		if (address != null && !address.isEmpty()) {
			Point point = KakaoApiUtil.getPointByAddress(address);
			model.addAttribute("point", point);
		}
		return "map/address_point";
	}

	/**
	 * 출발지와 목적지를 지도상에 표시하기
	 * 
	 * @param fromAddress 출발지 주소정보
	 * @param toAddress   목적지 주소정보
	 * @param model       html파일에 값을 전달해주는 객체
	 * @return html 파일위치
	 * 
	 */
	@GetMapping("/map/marker") // url : /map/marker
	public String getMapMarker(@RequestParam(required = false) String fromAddress, //
			@RequestParam(required = false) String toAddress, //
			Model model) throws IOException, InterruptedException {
		if (fromAddress != null && !fromAddress.isEmpty()) {
			Point fromPoint = KakaoApiUtil.getPointByAddress(fromAddress);
			model.addAttribute("fromPoint", fromPoint);
		}
		if (toAddress != null && !toAddress.isEmpty()) {
			Point toPoint = KakaoApiUtil.getPointByAddress(toAddress);
			model.addAttribute("toPoint", toPoint);
		}
		return "map/marker";
	}
	
	
	   @GetMapping("/map/search")
	    public String getKeyword(@RequestParam(required = false) String keyword, //keyword를 입력받음
	                             @RequestParam(required = false) String x, //x좌표를 입력받음
	                             @RequestParam(required = false) String y, Model model) throws IOException, InterruptedException { //y좌표를 입력받음
		   System.out.println("x");
		   System.out.println(x);
		   System.out.println("y");
		   System.out.println(y);
	        if (keyword != null && !keyword.isEmpty() &&
	            x != null && !x.isEmpty() &&
	            y != null && !y.isEmpty()) { //keyword, x, y값이 모두 입력되었을 때 실행
	            List<KakaoApiUtil.Pharmacy> pharmacyList = KakaoApiUtil.searchPointByAddress(keyword, x, y); //keyword, x, y값을 getPointsByKeyword에 넣어서 반환되는 Pharmacy로 구성된 List를 저장
	            int cnt = 0; //pharmacyList의 크기를 저장할 변수
	            assert pharmacyList != null;//pharmacyList가 null이 아닐 때 실행
//	            ------------------------------수정 목록 -----------------------------------------//
	            List<Point> pointList = new ArrayList<>();
	            for (int i=1; i<pharmacyList.size(); i++) {
	            	KakaoApiUtil.Pharmacy prevPharmacy = pharmacyList.get(i-1);
	            	KakaoApiUtil.Pharmacy nextPharmacy = pharmacyList.get(i);
	            	Point fromPoint = new Point(prevPharmacy.getX(),prevPharmacy.getY());
	            	Point toPoint = new Point(nextPharmacy.getX(),nextPharmacy.getY());
	            	
	            	pointList.addAll(KakaoApiUtil.getVehiclePaths(fromPoint, toPoint));
	            }
//	            for (KakaoApiUtil.Pharmacy pharmacy : pharmacyList) { //pharmacyList의 크기만큼 반복
//	                cnt++; //pharmacyList의 크기를 저장
//	            }            
//	           System.out.println(cnt); //pharmacyList의 크기를 출력
	            String pointListjson = new ObjectMapper().writer().writeValueAsString(pointList);//pharmacyList를 json형태로 변환
	            String pharmacyListJson = new ObjectMapper().writer().writeValueAsString(pharmacyList);//pharmacyList를 json형태로 변환 
	            model.addAttribute("pointListJson", pointListjson);
	            model.addAttribute("pharmacyList", pharmacyListJson); //html로 보냄
	            System.out.println("실행됨"); //실행됐는지 확인
	        }
	        return "map/search"; //html 파일위치
	    }
	

}