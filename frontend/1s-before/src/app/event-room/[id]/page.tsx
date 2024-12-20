'use client';
import DoubleUpIcon from '@/components/icon/DoubleUpIcon';
import BannerHeader from '@/components/eventRoom/BannerHeader';
import EventStatusArea from '@/components/eventRoom/EventStatusArea';
import EventChatRoomArea from '@/components/eventRoom/EventChatRoomArea';
import EventResultAllResult from '@/components/eventRoom/EventResultAllResult';
import { useState, useEffect } from 'react';
import { useEventProgress } from '@/hooks/useEventProgress';

/**
 * IMP : Event의 진행 및 결과를 보여주는 Page
 * TYPE 1 : 해당 Event의 정보를 보여주는 BannerHeader
 * TYPE 2 : Event의 진행 상태를 보여주는 EventStatusArea, EventResultArea 포함되어 있음
 * TYPE 3 : Event의 채팅창을 보여주는 EventChatRoomArea
 * TYPE 4 : Event의 결과를 보여주는 EventResultAllResult
 * @returns
 */
export default function EventRoom() {
  const {
    messages,
    myResult,
    isDrawing,
    eventInfo,
    eventStatus,
    eventResult,
    untilMyResult,
    setIsDrawing,
    toggleChatSize,
    getUntilMyResult,
    getStatusAreaWidth,
    getChatRoomAreaWidth,
  } = useEventProgress();

  const competitionRate =
    eventInfo && eventStatus ? (eventStatus.userCount ?? 0) / eventInfo.winnerNum : 0;
  const [currentProccessed, setCurrentProccessed] = useState(0);
  const [showScrollToTop, setShowScrollToTop] = useState(false);
  const calculateCurrentProcessed = (processed: number) => setCurrentProccessed(processed);
  const addCalculatedCurrentProcessed = (processed: number) =>
    setCurrentProccessed((prev) => prev + processed);

  useEffect(() => {
    // 스크롤 이벤트 핸들러
    const handleScroll = () => {
      const scrollY = window.scrollY || document.documentElement.scrollTop;
      setShowScrollToTop(scrollY > 300); // 스크롤 위치에 따라 버튼 표시
    };

    window.addEventListener('scroll', handleScroll);
    return () => {
      window.removeEventListener('scroll', handleScroll);
    };
  }, []);

  const scrollToTop = () => {
    // 페이지 맨 위로 이동
    window.scrollTo({
      top: 0,
      behavior: 'smooth',
    });
  };

  return (
    <div className='flex flex-col max-w-screen-xl mx-auto px-7'>
      {eventInfo && (
        <div className='flex flex-col w-full gap-12 mb-7'>
          <BannerHeader bannerImage={eventInfo.bannerImage} />
          <div className='text-xl font-normal' style={{ color: '#1C1C1E', lineHeight: '33px' }}>
            [{eventInfo.creatorName}] <span className='font-bold'>{eventInfo.title}</span>
          </div>
          <div className='flex gap-5'>
            <div className={`transition-all duration-300 ${getStatusAreaWidth()}`}>
              <EventStatusArea
                myResult={myResult}
                isDrawing={isDrawing}
                eventTime={eventInfo.eventTime}
                eventId={eventInfo.eventId}
                getMyEventResult={getUntilMyResult}
                goDrawView={() => setIsDrawing(true)}
                currentProccessed={currentProccessed}
                competitionRate={competitionRate}
                totalParticipants={eventStatus?.userCount ?? 0}
              />

              <EventResultAllResult
                list={eventResult}
                untilMyResult={untilMyResult}
                myResult={myResult}
                eventTime={eventInfo.eventTime}
                calculateCurrentProcessed={calculateCurrentProcessed}
                addCalculatedCurrentProcessed={addCalculatedCurrentProcessed}
              />
            </div>
            <div className={`transition-all duration-300 cursor-pointer ${getChatRoomAreaWidth()}`}>
              <EventChatRoomArea
                eventId={eventInfo.eventId}
                participants={eventStatus?.userCount ?? 0}
                messages={messages}
                onClick={toggleChatSize}
              />
            </div>
          </div>
        </div>
      )}
      {/* 위로 가기 버튼 */}
      {showScrollToTop && (
        <button
          onClick={scrollToTop}
          className='fixed bottom-1 right-5 p-2 bg-[#474972] text-white rounded-full shadow-md'>
          <DoubleUpIcon />
        </button>
      )}
    </div>
  );
}
