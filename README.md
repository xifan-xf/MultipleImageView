# MultipleImageView
Like App SinaWeibo,Draw Bitmap as GridView.

![image](https://github.com/xufan/MultipleImageView/blob/master/image/show.gif)   

5、6、7 Images Format

![image](https://github.com/xufan/MultipleImageView/blob/master/image/1.png)
   
![image](https://github.com/xufan/MultipleImageView/blob/master/image/2.png)
   
![image](https://github.com/xufan/MultipleImageView/blob/master/image/3.png)

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
            public void onClick(int i) {
                Toast.makeText(mContext, "click on item:" + i, Toast.LENGTH_SHORT).show();
            }
        });
        
//when use RecyclerView free the memory        
@Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.mMulipleIv.setImageUrls(null);
    }        
```



