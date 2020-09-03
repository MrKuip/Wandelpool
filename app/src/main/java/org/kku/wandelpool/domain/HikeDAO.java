package org.kku.wandelpool.domain;


public class HikeDAO
{
  public HikeList selectHikeListFromCache(
      WandelpoolFilter filter)
      throws WandelpoolException
  {
    HikeList result;

    result = CacheManager.load();
    if (result == null)
    {
      result = selectHikeList();
    }

    return result;
  }

  public HikeList selectHikeList()
      throws WandelpoolException
  {
    return WandelpoolWebSite.getInstance().getHikeList();
  }
}
