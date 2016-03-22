# MultipleImageView
A single view draw like gridview
![image](https://github.com/xufan/MultipleImageView/blob/master/image/show.gif)   
# Usage
```xml
  <geek.fan.multipleimageview.ui.MultipleImageView xmlns:app="http://schemas.android.com/apk/res-auto"
        android:id="@+id/multiple_image"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:mivHorizontalSpace="4dp"
        app:mivRadius="5dp"
        app:mivVerticalSpace="4dp" />
```


```java
MultipleImageView.setImageUrls(urls);
MultipleImageView.setOnClickItemListener(new MultipleImageView.OnClickItemListener() {
            @Override
            public void onClick(int i, ArrayList<String> urls) {
                Toast.makeText(mContext, "click on item:" + i, Toast.LENGTH_SHORT).show();
            }
        });
```

