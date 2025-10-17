import com.dss.vms.common.constants.MediaType;

public class EnumValueTest {

	public static void main(String[] args) {
		MediaType type = MediaType.H264;
		System.out.println(type.value());
		System.out.println(MediaType.valueOf("MPEG4").value());
		System.out.println(MediaType.find((byte) 1));
	}
}
