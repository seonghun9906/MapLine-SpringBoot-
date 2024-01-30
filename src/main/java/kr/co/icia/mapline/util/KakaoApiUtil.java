package kr.co.icia.mapline.util;

import java.io.Console;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.util.RouteMatcher.Route;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.icia.mapline.dto.Places;
import kr.co.icia.mapline.util.KakaoApiUtil.KakaoDirections.Route.Section.Road;

public class KakaoApiUtil {
	private static final String REST_API_KEY = "b95bb41121e03f7a581d91f8a3a4d7ec";

	/**
	 * 자동차 길찾기
	 * 
	 * @param from 출발지
	 * @param to   도착지
	 * @return 이동 좌표 목록
	 * @throws InterruptedException
	 */

	public static List<Point> getVehiclePaths(Point from, Point to) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String url = "https://apis-navi.kakaomobility.com/v1/directions";
		url += "?origin=" + from.getX() + "," + from.getY();
		url += "&destination=" + to.getX() + "," + to.getY();
		HttpRequest request = HttpRequest.newBuilder()//
				.header("Authorization", "KakaoAK " + REST_API_KEY)//
				.header("Content-Type", "application/json")//
				.uri(URI.create(url))//
				.GET()//
				.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		String responseBody = response.body();

		System.out.println("자동차길찾기 응답 본문 : "+responseBody);
		
		List<Point> pointList = new ArrayList<Point>();

		KakaoDirections kakaoDirections = new ObjectMapper().readValue(responseBody, KakaoDirections.class);
		List<Road> roads = kakaoDirections.getRoutes().get(0).getSections().get(0).getRoads();
		for (Road road : roads) {
			List<Double> vertexes = road.getVertexes();
			for (int i = 0; i < vertexes.size(); i++) {
				pointList.add(new Point(vertexes.get(i), vertexes.get(++i)));
			}
		}

		return pointList;

	}

	/**
	 * 주소 -> 좌표 변환
	 * 
	 * @param address 주소
	 * @return 좌표
	 */
	public static Point getPointByAddress(String address) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String url = "https://dapi.kakao.com/v2/local/search/address.json"; // KaKao에 있는 Util 을 쓰려고 Url 요청
		url += "?query=" + URLEncoder.encode(address, "UTF-8"); // 주소를 만드는 역할
		HttpRequest request = HttpRequest.newBuilder() //Builder 는 생성자를 만들기 위하는 역할
				.header("Authorization", "KakaoAK " + REST_API_KEY) // 응답과 요청에는 header과 body가 필요함 ( 이부분은 header)
				.uri(URI.create(url)) // 이 부분은 body
				.GET() //
				.build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString()); //http 에 대한 api 요청
		System.out.println("response");
		System.out.println(response);
		String responseBody = response.body();
		System.out.println("responseBody");
		System.out.println(responseBody);
		
		KakaoAddress kakaoAddress = new ObjectMapper().readValue(responseBody, KakaoAddress.class);
		System.out.println("kakaoAddress");
		System.out.println(kakaoAddress);
		List<KakaoAddress.Document> documents = kakaoAddress.getDocuments();
		System.out.println("documents");
		System.out.println(documents);
		if (documents.isEmpty()) {
			return null;
		} // JSON 값을 가져와서 까기 위한 작업.
		KakaoAddress.Document document = documents.get(0);
		return new Point(document.getX(), document.getY());
	}

/**************************************************************************************************************************************/
	
	public static List<Pharmacy> searchPointByAddress(String keyword, String x, String y) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String url = "https://dapi.kakao.com/v2/local/search/keyword.json";
		//String url = "https://dapi.kakao.com/v2/local/search/address.json";
		url += "?query=" + URLEncoder.encode(keyword, "UTF-8")
		+"&x=" + Double.parseDouble("126.675113024566")//
		+"&y=" + Double.parseDouble("37.4388938204128")
		+"&radius=5000";
		
		
		HttpRequest request = HttpRequest.newBuilder()//
				.header("Authorization", "KakaoAK " + REST_API_KEY)//
				.header("Content-Type", "application/json")//
				.uri(URI.create(url))//
				.GET()//
				.build();
	//	HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		//String responseBody = response.body();
	//	System.out.println(responseBody);
		
		
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		String responseBody = response.body();
		System.out.println(responseBody);

		List<Pharmacy> pharmacyList = new ArrayList<>(); // 화면에 표시할 Pharmacy들을 저장할 pharmacyList 생성 저어기 밑에 pharmacy class 보고오기
		List<Places> set1s = new ArrayList<>(); // 장소를 나타나는 리스트
		System.out.println("여기입니다" + pharmacyList.size());
		
		Places places = new ObjectMapper().readValue(responseBody, Places.class); //응답받은 값(responseBody)을 Places 인스턴스에 저장
		List<Places.Document> documents = places.getDocuments(); //응답받은 값(places)을 documents(document로 이루어진 List)에 저장
		Places.Meta meta = places.getMeta(); //응답받은 값(places)을 meta에 저장
		if (documents.isEmpty() || meta == null) { // 값이 없을 경우 오류 가능성이 있어 null처리
			return null; //null 반환
		}
		
		int firstPageMarker = 15;
		 for (Places.Document document : documents) { //enhanced for문으로 documents안의 document들을 각각 탐색하여 Pharmacy 객체를 생성하고 pharmacyList에 저장
		//	 if(firstPageMarker > 0){
	            Pharmacy pharmacy = new Pharmacy(Double.parseDouble(document.getX()), //document의 x, y값을 Pharmacy 객체에 담아 pharmacyList에 저장
	                    Double.parseDouble(document.getY()), document.getPlace_name(), document.getPhone(), document.getPlace_url()); //document의 place_name, phone값을 Pharmacy 객체에 담아 pharmacyList에 저장
	            pharmacyList.add(pharmacy); //pharmacyList에 pharmacy 객체를 추가
	            firstPageMarker--;
	     //   }else {
		//	 break;
	     //   }
		 }
		 if (meta.getTotal_count() > 15) { //검색된 장소가 15개를 넘어갈 경우
	            for (int i = 1; i <= Math.min(meta.getTotal_count() /15,15); i++) { //15개씩 나눠서 검색
	                String url2 = url + "&page=" + (i + 1); //api 주소에 page값 추가
	                System.out.println(url2); //출력
	                HttpRequest request2 = HttpRequest.newBuilder() //api 요청 양식
	                        .header("Authorization", "KakaoAK " + REST_API_KEY) //api 요청 헤더(api 인증키 포함)
	                        .uri(URI.create(url2))//api 요청 주소
	                        .GET()//GET방식으로 가져옴
	                        .build();//api 요청 양식 완성
	                HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());//요청을 보내서 온 응답을 response에 저장
	                String responseBody2 = response2.body();//응답받은 값의 body값을 responseBody에 저장
	                System.out.println("keyword: " + responseBody2);//출력
	                Places places2 = new ObjectMapper().readValue(responseBody2, Places.class);//응답받은 값(responseBody2)을 Places 인스턴스에 저장
	                
	                List<Places.Document> documents2 = places2.getDocuments();//응답받은 값(places2)을 documents2(document로 이루어진 List)에 저장
	                Places.Meta meta2 = places2.getMeta();//응답받은 값(places2)을 meta2에 저장
	                if (documents2.isEmpty() || meta2 == null) {// 값이 없을 경우 오류 가능성이 있어 null처리
	                    return null;//null 반환
	                }
	                
	                for (Places.Document document : documents2) {//enhanced for문으로 documents2안의 document들을 각각 탐색하여 Pharmacy 객체를 생성하고 pharmacyList에 저장
	                	if(firstPageMarker>0){
	                    Pharmacy pharmacy2 = new Pharmacy(Double.parseDouble(document.getX()),//document의 x, y값을 Pharmacy 객체에 담아 pharmacyList에 저장
	                            Double.parseDouble(document.getY()), document.getPlace_name(), document.getPhone(), document.getPlace_url());//document의 place_name, phone값을 Pharmacy 객체에 담아 pharmacyList에 저장
	                    pharmacyList.add(pharmacy2);//pharmacyList에 pharmacy 객체를 추가
	                }else {
	                	break;
	                }
	             }
	          }
	      }
//		KakaoAddress kakaoAddress = new ObjectMapper().readValue(responseBody, KakaoAddress.class);
//		List<KakaoAddress.Document> documents = kakaoAddress.getDocuments();
//		if (documents.isEmpty()) {
//			return null;
//		}
//		KakaoAddress.Document document = documents.get(0);
//		return new Point(document.getX(), document.getY());
//	}
		return pharmacyList;
		

}
	 @JsonIgnoreProperties(ignoreUnknown = true) //api로 받아온 json값에서 원하는 값(vertexes)만 추출하기 위해 class안에 class가 들어가는 식으로 분리함
	    public static class KakaoDirections {       // KakaoDiretions 안에 Route 요소 안에 Section 요소 안에 Road요소 안에 vertexes가 들어있는 구조 (마트료시카?)
	        private List<Route> routes;

	        public List<Route> getRoutes() {
	            return routes;
	        }

	        @JsonIgnoreProperties(ignoreUnknown = true)
	        public static class Route {
	            private List<Section> sections;

	            public List<Section> getSections() {
	                return sections;
	            }

	            @JsonIgnoreProperties(ignoreUnknown = true)
	            public static class Section {
	                private List<Road> roads;

	                public List<Road> getRoads() {
	                    return roads;
	                }

	                @JsonIgnoreProperties(ignoreUnknown = true)
	                public static class Road {
	                    private List<Double> vertexes;

	                    public List<Double> getVertexes() {
	                        return vertexes;
	                    }

	                }

	            }
	        }
	    }

	    @JsonIgnoreProperties(ignoreUnknown = true)
	    private static class KakaoAddress {
	        private List<Document> documents; //(x, y로 이루어진 Document)의 List

	        public List<Document> getDocuments() {
	            return documents;
	        }
	        
	        

	        @Override
			public String toString() {
				return "KakaoAddress [documents=" + documents + "]";
			}



			@JsonIgnoreProperties(ignoreUnknown = true)
	        public static class Document {
	            private Double x;
	            private Double y;

	            @Override
				public String toString() {
					return "Document [x=" + x + ", y=" + y + "]";
				}

				public Double getX() {
	                return x;
	            }

	            public Double getY() {
	                return y;
	            }
	        }
	    }

	    public static class Point {
	        private Double x;
	        private Double y;

	        @Override
			public String toString() {
				return "Point [x=" + x + ", y=" + y + "]";
			}

			public Point(Double x, Double y) {
	            this.x = x;
	            this.y = y;
	        }

	        public Double getX() {
	            return x;
	        }

	        public Double getY() {
	            return y;
	        }

	    }


	    public static class Pharmacy { //Pharmacy class는 x, y, name, tel로 이루어져 있음
	        private Double x; //x값
	        private Double y; //y값
	        private String name; //이름
	        private String tel; //전화번호

	        private String url; //url

	        public Double getX() {
	            return x;
	        }

	        public Double getY() {
	            return y;
	        }

	        public String getName() {
	            return name;
	        }

	        public String getUrl() {
	            return url;
	        }

	        public String getTel() {
	            return tel;
	        }

	        public Pharmacy(Double x, Double y, String name, String tel, String url) {
	            this.x = x;
	            this.y = y;
	            this.name = name;
	            this.tel = tel;
	            this.url = url;
	        }
	    }
	}